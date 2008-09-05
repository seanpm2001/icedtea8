/*
 * Copyright 2003-2007 Sun Microsystems, Inc.  All Rights Reserved.
 * Copyright 2007, 2008 Red Hat, Inc.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 *
 */

// do not include precompiled header file
#include "incls/_os_linux_zero.cpp.incl"

address os::current_stack_pointer()
{
  address dummy = (address) &dummy;
  return dummy;
}

frame os::get_sender_for_C_frame(frame* fr)
{
  Unimplemented();
}

frame os::current_frame()
{
  Unimplemented();
}

char* os::non_memory_address_word()
{
  // Must never look like an address returned by reserve_memory,
  // even in its subfields (as defined by the CPU immediate fields,
  // if the CPU splits constants across multiple instructions).
  Unimplemented();
}

void os::initialize_thread()
{
  // Nothing to do.
}

address os::Linux::ucontext_get_pc(ucontext_t* uc)
{
  Unimplemented();
}

ExtendedPC os::fetch_frame_from_context(void* ucVoid,
                                        intptr_t** ret_sp,
                                        intptr_t** ret_fp) {
  Unimplemented();
}

frame os::fetch_frame_from_context(void* ucVoid)
{
  Unimplemented();
}

extern "C" int
JVM_handle_linux_signal(int sig,
                        siginfo_t* info,
                        void* ucVoid,
                        int abort_if_unrecognized)
{
  ucontext_t* uc = (ucontext_t*) ucVoid;

  Thread* t = ThreadLocalStorage::get_thread_slow();

  SignalHandlerMark shm(t);

  // Note: it's not uncommon that JNI code uses signal/sigset to
  // install then restore certain signal handler (e.g. to temporarily
  // block SIGPIPE, or have a SIGILL handler when detecting CPU
  // type). When that happens, JVM_handle_linux_signal() might be
  // invoked with junk info/ucVoid. To avoid unnecessary crash when
  // libjsig is not preloaded, try handle signals that do not require
  // siginfo/ucontext first.

  if (sig == SIGPIPE || sig == SIGXFSZ) {
    // allow chained handler to go first
    if (os::Linux::chained_handler(sig, info, ucVoid)) {
      return true;
    } else {
      if (PrintMiscellaneous && (WizardMode || Verbose)) {
        char buf[64];
        warning("Ignoring %s - see bugs 4229104 or 646499219",
                os::exception_name(sig, buf, sizeof(buf)));
      }
      return true;
    }
  }

  JavaThread* thread = NULL;
  VMThread* vmthread = NULL;
  if (os::Linux::signal_handlers_are_installed) {
    if (t != NULL ){
      if(t->is_Java_thread()) {
        thread = (JavaThread*)t;
      }
      else if(t->is_VM_thread()){
        vmthread = (VMThread *)t;
      }
    }
  }

  if (info != NULL && thread != NULL) {
    // Check to see if we caught the safepoint code in the process
    // of write protecting the memory serialization page.  It write
    // enables the page immediately after protecting it so we can
    // just return to retry the write.
    if (sig == SIGSEGV &&
        os::is_memory_serialize_page(thread, (address) info->si_addr)) {
      // Block current thread until permission is restored.
      os::block_on_serialize_page_trap();
      return true;
    }
  }

#ifndef PRODUCT
  if (sig == SIGSEGV) {
    fatal("\n#"
          "\n#    /--------------------\\"
          "\n#    | segmentation fault |"
          "\n#    \\---\\ /--------------/"
          "\n#        /"
          "\n#    [-]        |\\_/|    "
          "\n#    (+)=C      |o o|__  "
          "\n#    | |        =-*-=__\\ "
          "\n#    OOO        c_c_(___)");
  }
#endif // !PRODUCT
  
  const char *fmt = "caught unhandled signal %d";
  char buf[64];

  sprintf(buf, fmt, sig);
  fatal(buf);
}

void os::Linux::init_thread_fpu_state(void)
{
  // Nothing to do
}

int os::Linux::get_fpu_control_word()
{
  Unimplemented();
}

void os::Linux::set_fpu_control_word(int fpu)
{
  Unimplemented();
}

///////////////////////////////////////////////////////////////////////////////
// thread stack

size_t os::Linux::min_stack_allowed = 64 * K;

bool os::Linux::supports_variable_stack_size()
{
  return true;
}

size_t os::Linux::default_stack_size(os::ThreadType thr_type)
{
#ifdef _LP64
  size_t s = (thr_type == os::compiler_thread ? 4 * M : 1 * M);
#else
  size_t s = (thr_type == os::compiler_thread ? 2 * M : 512 * K);
#endif // _LP64
  return s;
}

size_t os::Linux::default_guard_size(os::ThreadType thr_type)
{
  // Only enable glibc guard pages for non-Java threads
  // (Java threads have HotSpot guard pages)
  return (thr_type == java_thread ? 0 : page_size());
}

static void current_stack_region(address *bottom, size_t *size)
{
  pthread_attr_t attr;
  int res = pthread_getattr_np(pthread_self(), &attr);
  if (res != 0) {
    if (res == ENOMEM) {
      vm_exit_out_of_memory(0, "pthread_getattr_np");
    }
    else {
      fatal1("pthread_getattr_np failed with errno = %d", res);
    }
  }

  address stack_bottom;
  size_t stack_bytes;
  res = pthread_attr_getstack(&attr, (void **) &stack_bottom, &stack_bytes);
  if (res != 0) {
    fatal1("pthread_attr_getstack failed with errno = %d", res);
  }
  address stack_top = stack_bottom + stack_bytes;

  // The block of memory returned by pthread_attr_getstack() includes
  // guard pages where present.  We need to trim these off.
  size_t page_bytes = os::Linux::page_size();
  assert(((intptr_t) stack_bottom & (page_bytes - 1)) == 0, "unaligned stack");

  size_t guard_bytes;
  res = pthread_attr_getguardsize(&attr, &guard_bytes);
  if (res != 0) {
    fatal1("pthread_attr_getguardsize failed with errno = %d", res);
  }
  int guard_pages = align_size_up(guard_bytes, page_bytes) / page_bytes;
  assert(guard_bytes == guard_pages * page_bytes, "unaligned guard");

#ifdef IA64
  // IA64 has two stacks sharing the same area of memory, a normal
  // stack growing downwards and a register stack growing upwards.
  // Guard pages, if present, are in the centre.  This code splits
  // the stack in two even without guard pages, though in theory
  // there's nothing to stop us allocating more to the normal stack
  // or more to the register stack if one or the other were found
  // to grow faster.
  int total_pages = align_size_down(stack_bytes, page_bytes) / page_bytes;
  stack_bottom += (total_pages - guard_pages) / 2 * page_bytes;
#endif // IA64

  stack_bottom += guard_bytes;

  pthread_attr_destroy(&attr);

  // The initial thread has a growable stack, and the size reported
  // by pthread_attr_getstack is the maximum size it could possibly
  // be given what currently mapped.  This can be huge, so we cap it.
  if (os::Linux::is_initial_thread()) {
    stack_bytes = stack_top - stack_bottom;

    if (stack_bytes > JavaThread::stack_size_at_create())
      stack_bytes = JavaThread::stack_size_at_create();

    stack_bottom = stack_top - stack_bytes;
  }

  assert(os::current_stack_pointer() >= stack_bottom, "should do");
  assert(os::current_stack_pointer() < stack_top, "should do");

  *bottom = stack_bottom;
  *size = stack_top - stack_bottom;
}

address os::current_stack_base()
{
  address bottom;
  size_t size;
  current_stack_region(&bottom, &size);
  return bottom + size;
}

size_t os::current_stack_size()
{
  // stack size includes normal stack and HotSpot guard pages
  address bottom;
  size_t size;
  current_stack_region(&bottom, &size);
  return size;
}

/////////////////////////////////////////////////////////////////////////////
// helper functions for fatal error handler

void os::print_context(outputStream* st, void* context)
{
  Unimplemented();
}

/////////////////////////////////////////////////////////////////////////////
// Stubs for things that would be in linux_zero.s if it existed.
// You probably want to disassemble these monkeys to check they're ok.

extern "C" {
  int SpinPause()
  {
  }

  int SafeFetch32(int *adr, int errValue)
  {
    int value = errValue;
    value = *adr;
    return value;
  }
  intptr_t SafeFetchN(intptr_t *adr, intptr_t errValue)
  {
    intptr_t value = errValue;
    value = *adr;
    return value;
  }

  void _Copy_conjoint_jshorts_atomic(jshort* from, jshort* to, size_t count)
  {
    Unimplemented();
  }
  void _Copy_conjoint_jints_atomic(jint* from, jint* to, size_t count)
  {
    if (from > to) {
      jint *end = from + count;
      while (from < end)
        *(to++) = *(from++);
    }
    else if (from < to) {
      jint *end = from;
      from += count - 1;
      to   += count - 1;
      while (from >= end)
        *(to--) = *(from--);
    }
  }
  void _Copy_conjoint_jlongs_atomic(jlong* from, jlong* to, size_t count)
  {
    if (from > to) {
      jlong *end = from + count;
      while (from < end)
        *(to++) = *(from++);
    }
    else if (from < to) {
      jlong *end = from;
      from += count - 1;
      to   += count - 1;
      while (from >= end)
        *(to--) = *(from--);
    }
  }

  void _Copy_arrayof_conjoint_bytes(HeapWord* from, HeapWord* to, size_t count)
  {
    Unimplemented();
  }
  void _Copy_arrayof_conjoint_jshorts(HeapWord* from, HeapWord* to,
                                      size_t count) {
    Unimplemented();
  }
  void _Copy_arrayof_conjoint_jints(HeapWord* from, HeapWord* to, size_t count)
  {
    Unimplemented();
  }
  void _Copy_arrayof_conjoint_jlongs(HeapWord* from, HeapWord* to,
                                     size_t count) {
    Unimplemented();
  }
};

/////////////////////////////////////////////////////////////////////////////
// Implementations of atomic operations not supported by processors.
//  -- http://gcc.gnu.org/onlinedocs/gcc-4.2.1/gcc/Atomic-Builtins.html

#ifndef _LP64
extern "C" {
  long long unsigned int __sync_val_compare_and_swap_8(
    volatile void *ptr,
    long long unsigned int oldval,
    long long unsigned int newval)
  {
    Unimplemented();
  }
};
#endif // !_LP64
