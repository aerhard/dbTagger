/**
 * Copyright 2013 Alexander Erhard
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aerhard.oxygen.plugin.dbtagger;

import ro.sync.exml.plugin.Plugin;
import ro.sync.exml.plugin.PluginDescriptor;

/**
 * The dbTagger plugin class.
 */
public class TaggerPlugin extends Plugin {
    /**
     * The static plugin instance.
     */
    private static TaggerPlugin instance = null;

    /**
     * Constructs the plugin.
     * 
     * @param descriptor
     *            The plugin descriptor
     */
    public TaggerPlugin(PluginDescriptor descriptor) {
        super(descriptor);

        if (instance != null) {
            throw new IllegalStateException("Already instantiated!");
        }
        instance = this;
    }

    /**
     * Gets the plugin instance.
     * 
     * @return the shared plugin instance.
     */
    public static TaggerPlugin getInstance() {
        return instance;
    }
}
