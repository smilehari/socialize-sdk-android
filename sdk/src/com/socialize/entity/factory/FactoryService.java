/*
 * Copyright (c) 2011 SocializeService Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.socialize.entity.factory;

import java.util.Map;

import com.socialize.entity.SocializeObject;

/**
 * @author Jason Polites
 * @deprecated No need to have a cache for this.  We can just inject the factory using IOC.
 */
public class FactoryService {

	private Map<String, SocializeObjectFactory<?>> factories;
	
	public FactoryService() {
		super();
	}

	@SuppressWarnings("unchecked")
	public <T extends SocializeObject, F extends SocializeObjectFactory<T>> F getFactoryFor(Class<T> clazz) {
		F factory = (F) factories.get(clazz.getName());
		return factory;
	}
	
	public Map<String, SocializeObjectFactory<?>> getFactories() {
		return factories;
	}

	public void setFactories(Map<String, SocializeObjectFactory<?>> factories) {
		this.factories = factories;
	}
}
