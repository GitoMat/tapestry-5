// Copyright 2009, 2012 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.apache.tapestry5.internal.services;

import org.apache.tapestry5.ioc.internal.util.CollectionFactory;
import org.apache.tapestry5.services.ComponentClasses;
import org.apache.tapestry5.services.InvalidationEventHub;

import javax.annotation.PostConstruct;

import java.util.Map;

public class StringInternerImpl implements StringInterner
{
    private final Map<String, String> cache = CollectionFactory.newConcurrentMap();

    @PostConstruct
    public void setupInvalidation(@ComponentClasses InvalidationEventHub hub)
    {
        hub.clearOnInvalidation(cache);
    }

    public String intern(String string)
    {
        String result = cache.get(string);

        // Not yet in the cache?  Add it.

        if (result == null)
        {
            cache.put(string, string);
            result = string;
        }

        return result;
    }

    public String format(String format, Object... arguments)
    {
        return intern(String.format(format, arguments));
    }
}
