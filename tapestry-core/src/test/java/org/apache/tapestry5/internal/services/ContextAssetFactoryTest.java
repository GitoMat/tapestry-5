// Copyright 2006, 2007, 2009, 2012 The Apache Software Foundation
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

package org.apache.tapestry5.internal.services;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.internal.services.assets.AssetPathConstructorImpl;
import org.apache.tapestry5.internal.test.InternalBaseTestCase;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.services.AssetFactory;
import org.apache.tapestry5.services.BaseURLSource;
import org.apache.tapestry5.services.Context;
import org.apache.tapestry5.services.Request;
import org.testng.annotations.Test;

public class ContextAssetFactoryTest extends InternalBaseTestCase
{
    private final IdentityAssetPathConverter converter = new IdentityAssetPathConverter();

    @Test
    public void root_resource()
    {
        Context context = mockContext();
        // Request request = mockRequest();

        replay();

        AssetFactory factory = new ContextAssetFactory(null, context, converter);

        assertEquals(factory.getRootResource().toString(), "context:/");

        verify();
    }

    @Test
    public void asset_client_URL()
    {
        Context context = mockContext();
        Request request = mockRequest();

        BaseURLSource baseURLSource = newMock(BaseURLSource.class);

        Resource r = new ContextResource(context, "foo/Bar.txt");

        replay();

        AssetFactory factory = new ContextAssetFactory(
                new AssetPathConstructorImpl(request,
                        baseURLSource,
                        "/context", "4.5.6",
                        "",
                        false,
                        "assets"
                ),
                context,
                new IdentityAssetPathConverter()
        );

        Asset asset = factory.createAsset(r);

        assertSame(asset.getResource(), r);
        assertEquals(asset.toClientURL(), "/context/assets/4.5.6/ctx/foo/Bar.txt");

        // In real life, toString() is the same as toClientURL(), but we're testing
        // that the optimize method is getting called, basically.

        assertEquals(asset.toString(), "/context/assets/4.5.6/ctx/foo/Bar.txt");

        verify();
    }

    @Test
    public void asset_client_URL_with_default_context() {
        Context context = mockContext();
        Request request = mockRequest();

        BaseURLSource baseURLSource = newMock(BaseURLSource.class);

        Resource r = new ContextResource(context, "foo/Bar.txt");

        replay();

        AssetFactory factory = new ContextAssetFactory(
                new AssetPathConstructorImpl(request,
                        baseURLSource,
                        "", "4.5.6",
                        "",
                        false,
                        "assets"
                ),
                context,
                new IdentityAssetPathConverter()
        );

        Asset asset = factory.createAsset(r);

        assertEquals(asset.toClientURL(), "/assets/4.5.6/ctx/foo/Bar.txt");

        verify();
    }

    @Test
    public void asset_client_URL_fully_qualified()
    {
        Context context = mockContext();
        Request request = mockRequest();

        BaseURLSource baseURLSource = newMock(BaseURLSource.class);

        Resource r = new ContextResource(context, "foo/Bar.txt");

        train_getBaseSource(baseURLSource, request);

        replay();

        AssetFactory factory = new ContextAssetFactory(
                new AssetPathConstructorImpl(request,
                        baseURLSource,
                        "/context", "4.5.6",
                        "",
                        true,
                        "assets"
                ),
                context,
                new IdentityAssetPathConverter()
        );

        Asset asset = factory.createAsset(r);

        assertSame(asset.getResource(), r);
        assertEquals(asset.toClientURL(), "/context/assets/4.5.6/ctx/foo/Bar.txt");

        // In real life, toString() is the same as toClientURL(), but we're testing
        // that the optimize method is getting called, basically.

        assertEquals(asset.toString(), "/context/assets/4.5.6/ctx/foo/Bar.txt");

        verify();
    }
}
