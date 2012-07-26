/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.karaf;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;

import java.util.Dictionary;

public abstract class Factory<T> {

    protected BundleContext bundleContext;
    protected T service;
    protected ServiceRegistration registration;

    public BundleContext getBundleContext() {
        return this.bundleContext;
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public void create(Dictionary properties) throws ConfigurationException {
        delete();
        try {
            service = doCreate(properties);
            try {
                registration = bundleContext.registerService(service.getClass().getName(), service, properties);
            } catch (Throwable t) {
                delete();
            }
        } catch (Throwable t) {
            delete();
            throw new RuntimeException("Unable to create service", t);
        }
    }

    protected abstract T doCreate(Dictionary properties) throws Exception;

    public void delete() {
        if (registration != null) {
            try {
                registration.unregister();
            } catch (Throwable t) {
                // ignore
            }
            registration = null;
        }
        if (service != null) {
            try {
                doDelete(service);
            } catch (Throwable t) {
                // ignore
            }
            service = null;
        }
    }

    protected abstract void doDelete(T service) throws Exception;

}
