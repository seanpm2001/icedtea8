/*
 * Copyright 2002 Sun Microsystems, Inc.  All Rights Reserved.
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

// Platform specific for C++ based Interpreter
#define LOTS_OF_REGS   // Use plenty of registers

 private:
  interpreterState _self_link;

 public:
  static ByteSize stack_limit_offset()
  {
    return byte_offset_of(BytecodeInterpreter, _stack_limit);
  }

#ifdef PPC
// The frame manager handles this
#define SET_LAST_JAVA_FRAME()
#define RESET_LAST_JAVA_FRAME()
#else
#define SET_LAST_JAVA_FRAME()   Unimplemented()
#define RESET_LAST_JAVA_FRAME() Unimplemented()
#endif // PPC

/*
 * Macros for accessing the stack.
 */
#undef STACK_INT
#undef STACK_FLOAT
#undef STACK_ADDR
#undef STACK_OBJECT
#undef STACK_DOUBLE
#undef STACK_LONG

// JavaStack Implementation

#define GET_STACK_SLOT(offset)    (*((intptr_t*) &topOfStack[-(offset)]))
#define STACK_SLOT(offset)    ((address) &topOfStack[-(offset)])
#define STACK_ADDR(offset)    (*((address *) &topOfStack[-(offset)]))
#define STACK_INT(offset)     (*((jint*) &topOfStack[-(offset)]))
#define STACK_FLOAT(offset)   (*((jfloat *) &topOfStack[-(offset)]))
#define STACK_OBJECT(offset)  (*((oop *) &topOfStack [-(offset)]))
#define STACK_DOUBLE(offset)  (((VMJavaVal64*) &topOfStack[-(offset)])->d)
#define STACK_LONG(offset)    (((VMJavaVal64 *) &topOfStack[-(offset)])->l)

#define SET_STACK_SLOT(value, offset)   (*(intptr_t*)&topOfStack[-(offset)] = *(intptr_t*)(value))
#define SET_STACK_ADDR(value, offset)   (*((address *)&topOfStack[-(offset)]) = (value))
#define SET_STACK_INT(value, offset)    (*((jint *)&topOfStack[-(offset)]) = (value))
#define SET_STACK_FLOAT(value, offset)  (*((jfloat *)&topOfStack[-(offset)]) = (value))
#define SET_STACK_OBJECT(value, offset) (*((oop *)&topOfStack[-(offset)]) = (value))
#define SET_STACK_DOUBLE(value, offset) (((VMJavaVal64*)&topOfStack[-(offset)])->d = (value))
#define SET_STACK_DOUBLE_FROM_ADDR(addr, offset) (((VMJavaVal64*)&topOfStack[-(offset)])->d =  \
                                                 ((VMJavaVal64*)(addr))->d)
#define SET_STACK_LONG(value, offset)   (((VMJavaVal64*)&topOfStack[-(offset)])->l = (value))
#define SET_STACK_LONG_FROM_ADDR(addr, offset)   (((VMJavaVal64*)&topOfStack[-(offset)])->l =  \
                                                 ((VMJavaVal64*)(addr))->l)
// JavaLocals implementation

#define LOCALS_SLOT(offset)    ((intptr_t*)&locals[-(offset)])
#define LOCALS_ADDR(offset)    ((address)locals[-(offset)])
#define LOCALS_INT(offset)     (*((jint*)&locals[-(offset)]))
#define LOCALS_FLOAT(offset)   (*((jfloat*)&locals[-(offset)]))
#define LOCALS_OBJECT(offset)  ((oop)locals[-(offset)])
#define LOCALS_DOUBLE(offset)  (((VMJavaVal64*)&locals[-((offset) + 1)])->d)
#define LOCALS_LONG(offset)    (((VMJavaVal64*)&locals[-((offset) + 1)])->l)
#define LOCALS_LONG_AT(offset) (((address)&locals[-((offset) + 1)]))
#define LOCALS_DOUBLE_AT(offset) (((address)&locals[-((offset) + 1)]))

#define SET_LOCALS_SLOT(value, offset)    (*(intptr_t*)&locals[-(offset)] = *(intptr_t *)(value))
#define SET_LOCALS_ADDR(value, offset)    (*((address *)&locals[-(offset)]) = (value))
#define SET_LOCALS_INT(value, offset)     (*((jint *)&locals[-(offset)]) = (value))
#define SET_LOCALS_FLOAT(value, offset)   (*((jfloat *)&locals[-(offset)]) = (value))
#define SET_LOCALS_OBJECT(value, offset)  (*((oop *)&locals[-(offset)]) = (value))
#define SET_LOCALS_DOUBLE(value, offset)  (((VMJavaVal64*)&locals[-((offset)+1)])->d = (value))
#define SET_LOCALS_LONG(value, offset)	  (((VMJavaVal64*)&locals[-((offset)+1)])->l = (value))
#define SET_LOCALS_DOUBLE_FROM_ADDR(addr, offset) (((VMJavaVal64*)&locals[-((offset)+1)])->d = \
                                                  ((VMJavaVal64*)(addr))->d)
#define SET_LOCALS_LONG_FROM_ADDR(addr, offset) (((VMJavaVal64*)&locals[-((offset)+1)])->l = \
                                                ((VMJavaVal64*)(addr))->l)


