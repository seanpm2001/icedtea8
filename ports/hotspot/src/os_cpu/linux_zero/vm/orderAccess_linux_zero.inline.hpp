/*
 * Copyright 2003 Sun Microsystems, Inc.  All Rights Reserved.
 * Copyright 2007 Red Hat, Inc.
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

inline void OrderAccess::loadload()   { acquire(); }
inline void OrderAccess::storestore() { release(); }
inline void OrderAccess::loadstore()  { acquire(); }
inline void OrderAccess::storeload()  { fence(); }

inline void OrderAccess::acquire()
{
  __sync_synchronize();
}

inline void OrderAccess::release()
{
  __sync_synchronize();
}

inline void OrderAccess::fence()
{
  __sync_synchronize();
}

inline jbyte    OrderAccess::load_acquire(volatile jbyte*   p) { return *p; }
inline jshort   OrderAccess::load_acquire(volatile jshort*  p) { return *p; }
inline jint     OrderAccess::load_acquire(volatile jint*    p) { return *p; }
inline jlong    OrderAccess::load_acquire(volatile jlong*   p)
{
  jlong tmp;
  os::atomic_copy64(p, &tmp);
  return tmp;
}
inline jubyte   OrderAccess::load_acquire(volatile jubyte*  p) { return *p; }
inline jushort  OrderAccess::load_acquire(volatile jushort* p) { return *p; }
inline juint    OrderAccess::load_acquire(volatile juint*   p) { return *p; }
inline julong   OrderAccess::load_acquire(volatile julong*  p)
{
  julong tmp;
  os::atomic_copy64(p, &tmp);
  return tmp;
}
inline jfloat   OrderAccess::load_acquire(volatile jfloat*  p) { return *p; }
inline jdouble  OrderAccess::load_acquire(volatile jdouble* p)
{
  jdouble tmp;
  os::atomic_copy64(p, &tmp);
  return tmp;
}

inline intptr_t OrderAccess::load_ptr_acquire(volatile intptr_t*   p) { return *p; }
inline void*    OrderAccess::load_ptr_acquire(volatile void*       p) { return *(void* volatile *)p; }
inline void*    OrderAccess::load_ptr_acquire(const volatile void* p) { return *(void* const volatile *)p; }

inline void     OrderAccess::release_store(volatile jbyte*   p, jbyte   v) { *p = v; }
inline void     OrderAccess::release_store(volatile jshort*  p, jshort  v) { *p = v; }
inline void     OrderAccess::release_store(volatile jint*    p, jint    v) { *p = v; }
inline void     OrderAccess::release_store(volatile jlong*   p, jlong   v) { os::atomic_copy64(&v, p); }
inline void     OrderAccess::release_store(volatile jubyte*  p, jubyte  v) { *p = v; }
inline void     OrderAccess::release_store(volatile jushort* p, jushort v) { *p = v; }
inline void     OrderAccess::release_store(volatile juint*   p, juint   v) { *p = v; }
inline void     OrderAccess::release_store(volatile julong*  p, julong  v) { os::atomic_copy64(&v, p); }
inline void     OrderAccess::release_store(volatile jfloat*  p, jfloat  v) { *p = v; }
inline void     OrderAccess::release_store(volatile jdouble* p, jdouble v) { os::atomic_copy64(&v, p); }

inline void     OrderAccess::release_store_ptr(volatile intptr_t* p, intptr_t v) { *p = v; }
inline void     OrderAccess::release_store_ptr(volatile void*     p, void*    v) { *(void* volatile *)p = v; }

inline void     OrderAccess::store_fence(jbyte*   p, jbyte   v) { *p = v; fence(); }
inline void     OrderAccess::store_fence(jshort*  p, jshort  v) { *p = v; fence(); }
inline void     OrderAccess::store_fence(jint*    p, jint    v) { *p = v; fence(); }
inline void     OrderAccess::store_fence(jlong*   p, jlong   v) { os::atomic_copy64(&v, p); fence(); }
inline void     OrderAccess::store_fence(jubyte*  p, jubyte  v) { *p = v; fence(); }
inline void     OrderAccess::store_fence(jushort* p, jushort v) { *p = v; fence(); }
inline void     OrderAccess::store_fence(juint*   p, juint   v) { *p = v; fence(); }
inline void     OrderAccess::store_fence(julong*  p, julong  v) { os::atomic_copy64(&v, p); fence(); }
inline void     OrderAccess::store_fence(jfloat*  p, jfloat  v) { *p = v; fence(); }
inline void     OrderAccess::store_fence(jdouble* p, jdouble v) { os::atomic_copy64(&v, p); fence(); }

inline void     OrderAccess::store_ptr_fence(intptr_t* p, intptr_t v) { *p = v; fence(); }
inline void     OrderAccess::store_ptr_fence(void**    p, void*    v) { *p = v; fence(); }

inline void     OrderAccess::release_store_fence(volatile jbyte*   p, jbyte   v) { release_store(p, v); fence(); }
inline void     OrderAccess::release_store_fence(volatile jshort*  p, jshort  v) { release_store(p, v); fence(); }
inline void     OrderAccess::release_store_fence(volatile jint*    p, jint    v) { release_store(p, v); fence(); }
inline void     OrderAccess::release_store_fence(volatile jlong*   p, jlong   v) { release_store(p, v); fence(); }
inline void     OrderAccess::release_store_fence(volatile jubyte*  p, jubyte  v) { release_store(p, v); fence(); }
inline void     OrderAccess::release_store_fence(volatile jushort* p, jushort v) { release_store(p, v); fence(); }
inline void     OrderAccess::release_store_fence(volatile juint*   p, juint   v) { release_store(p, v); fence(); }
inline void     OrderAccess::release_store_fence(volatile julong*  p, julong  v) { release_store(p, v); fence(); }
inline void     OrderAccess::release_store_fence(volatile jfloat*  p, jfloat  v) { release_store(p, v); fence(); }
inline void     OrderAccess::release_store_fence(volatile jdouble* p, jdouble v) { release_store(p, v); fence(); }

inline void     OrderAccess::release_store_ptr_fence(volatile intptr_t* p, intptr_t v) { release_store_ptr(p, v); fence(); }
inline void     OrderAccess::release_store_ptr_fence(volatile void*     p, void*    v) { release_store_ptr(p, v); fence(); }
