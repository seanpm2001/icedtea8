/*
 * Copyright 2007-2008 Sun Microsystems, Inc.  All Rights Reserved.
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
 */

import java.nio.file.spi.FileSystemProvider;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.net.URI;
import java.util.*;
import java.io.IOException;

public class TestProvider extends FileSystemProvider {

    private final FileSystem theFileSystem;

    public TestProvider(FileSystemProvider defaultProvider) {
        theFileSystem = new TestFileSystem(this);

    }


    public String getScheme() {
        return "file";
    }


    public FileSystem newFileSystem(URI uri, Map<String,?> env) {
        throw new RuntimeException("not implemented");
    }


    public FileSystem getFileSystem(URI uri) {
        return theFileSystem;
    }


    public Path getPath(URI uri) {
        throw new RuntimeException("not implemented");
    }

    static class TestFileSystem extends FileSystem {
        private final TestProvider provider;

        TestFileSystem(TestProvider provider) {
            this.provider = provider;
        }


        public FileSystemProvider provider() {
            return provider;
        }


        public void close() throws IOException {
            throw new RuntimeException("not implemented");
        }


        public boolean isOpen() {
            throw new RuntimeException("not implemented");
        }


        public boolean isReadOnly() {
            throw new RuntimeException("not implemented");
        }


        public String getSeparator() {
            throw new RuntimeException("not implemented");
        }


        public Iterable<Path> getRootDirectories() {
            throw new RuntimeException("not implemented");
        }


        public Iterable<FileStore> getFileStores() {
            throw new RuntimeException("not implemented");
        }


        public Set<String> supportedFileAttributeViews() {
            throw new RuntimeException("not implemented");
        }


        public Path getPath(String path) {
            throw new RuntimeException("not implemented");
        }


        public PathMatcher getNameMatcher(String syntax, String pattern) {
            throw new RuntimeException("not implemented");
        }


        public UserPrincipalLookupService getUserPrincipalLookupService() {
            throw new RuntimeException("not implemented");
        }


        public WatchService newWatchService() throws IOException {
            throw new RuntimeException("not implemented");
        }
    }

}
