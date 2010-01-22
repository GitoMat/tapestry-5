// Copyright 2006, 2007, 2008, 2009, 2010 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.apache.tapestry5.internal;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.internal.services.PersistentFieldManager;
import org.apache.tapestry5.internal.structure.Page;
import org.apache.tapestry5.internal.structure.PageResetListener;
import org.apache.tapestry5.internal.transform.ParameterConduit;
import org.apache.tapestry5.runtime.RenderQueue;
import org.apache.tapestry5.services.FieldValueConduit;

/**
 * An extension of {@link org.apache.tapestry5.ComponentResources} that represents additional
 * methods that are private
 * to the framework and not exposed in any public APIs.
 */
public interface InternalComponentResources extends ComponentResources,
        InternalComponentResourcesCommon
{
    /**
     * Get the current persisted value of the field.
     * 
     * @param fieldName
     *            the name of the field to access
     * @return the value stored for the field, or null if no value is currently stored
     */
    Object getFieldChange(String fieldName);

    /**
     * Checks to see if there is a value stored for the indicated field.
     */
    boolean hasFieldChange(String fieldName);

    /**
     * Posts a change to a persistent field. If the component is still loading, then this change is
     * ignored. Otherwise,
     * it is propagated, via the
     * {@link Page#persistFieldChange(org.apache.tapestry5.ComponentResources, String, Object)
     * page} to the {@link PersistentFieldManager}.
     */
    void persistFieldChange(String fieldName, Object newValue);

    /**
     * Allows the resources to cleanup any render-time only data.
     */
    void postRenderCleanup();

    /**
     * Invoked to make the receiver queue itself to be rendered.
     */
    void queueRender(RenderQueue queue);

    /**
     * Gets access object for the parameter.
     * 
     * @param parameterName
     * @return object used to read and update the parameter
     */
    ParameterAccess getParameterAccess(String parameterName);

    /**
     * Gets access object suitable for handling mixin fields which are bound to a parameter of the
     * core component
     * 
     * @param boundParameterName
     *            the name of the mixin field that should be linked to the core component's field.
     * @param parentParameterNames
     *            the list of parameter names to try in the parent. The first name that matches a
     *            declared parameter name in the core component will be used. This allows
     *            BindParameter to be used with
     *            mixins that have a similar parameter type with different parameter names (eg:
     * @since 5.2.0
     */
    ParameterAccess getContainerBoundParameterAccess(String boundParameterName,
            String... parentParameterNames);

    /**
     * Delegates to
     * {@link Page#addResetListener(org.apache.tapestry5.internal.structure.PageResetListener)}.
     * 
     * @param listener
     *            to register
     */
    void addPageResetListener(PageResetListener listener);

    /** @since 5.2.0 */
    FieldValueConduit getParameterConduit(String parameterName);

    /** @since 5.2.0 */
    void setParameterConduit(String parameterName, ParameterConduit conduit);
}
