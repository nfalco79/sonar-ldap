/*
 * SonarQube LDAP Plugin
 * Copyright (C) 2009-2021 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.plugins.ldap;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.sonar.api.config.Configuration;

public class TestConfiguration implements Configuration {

    Map<String, String> props = new HashMap<>();

    @Override
    public Optional<String> get(String key) {
        return Optional.ofNullable(props.get(key));
    }

    @Override
    public boolean hasKey(String key) {
        return props.containsKey(key);
    }

    @Override
    public String[] getStringArray(String key) {
        return hasKey(key) ? props.get(key).split("\\s*,\\s*") : new String[0];
    }

    public TestConfiguration setProperty(String key, String value) {
        props.put(key, value);
        return this;
    }

    public Configuration removeProperty(String key) {
        props.remove(key);
        return this;
    }

}
