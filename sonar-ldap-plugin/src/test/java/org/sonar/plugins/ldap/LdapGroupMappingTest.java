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

import org.junit.Test;
import org.sonar.api.config.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

public class LdapGroupMappingTest {

  @Test
  public void defaults() {
    LdapGroupMapping groupMapping = new LdapGroupMapping(new TestConfiguration(), "ldap");

    assertThat(groupMapping.getBaseDn()).isNull();
    assertThat(groupMapping.getIdAttribute()).isEqualTo("cn");
    assertThat(groupMapping.getMembershipAttribute()).isEqualTo(null);
    assertThat(groupMapping.getRequest()).isEqualTo("(&(objectClass=groupOfUniqueNames)(uniqueMember={0}))");
    assertThat(groupMapping.getRequiredUserAttributes()).isEqualTo(new String[] {"dn"});

    assertThat(groupMapping.toString()).isEqualTo("LdapGroupMapping{" +
      "baseDn=null," +
      " idAttribute=cn," +
      " membershipAttribute=null," +
      " requiredUserAttributes=[dn]," +
      " request=(&(objectClass=groupOfUniqueNames)(uniqueMember={0}))}");
  }

  @Test
  public void backward_compatibility() {
    Configuration settings = new TestConfiguration() //
      .setProperty("ldap.group.objectClass", "group") //
      .setProperty("ldap.group.memberAttribute", "member");
    LdapGroupMapping groupMapping = new LdapGroupMapping(settings, "ldap");

    assertThat(groupMapping.getRequest()).isEqualTo("(&(objectClass=group)(member={0}))");
  }

  @Test
  public void custom_request() {
    Configuration settings = new TestConfiguration() //
      .setProperty("ldap.group.request", "(&(|(objectClass=posixGroup)(objectClass=groupOfUniqueNames))(|(memberUid={uid})(uniqueMember={dn})))");
    LdapGroupMapping groupMapping = new LdapGroupMapping(settings, "ldap");

    assertThat(groupMapping.getRequest()).isEqualTo("(&(|(objectClass=posixGroup)(objectClass=groupOfUniqueNames))(|(memberUid={0})(uniqueMember={1})))");
    assertThat(groupMapping.getRequiredUserAttributes()).isEqualTo(new String[] {"uid", "dn"});
  }

}
