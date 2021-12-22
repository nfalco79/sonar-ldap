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

import org.apache.commons.lang.StringUtils;
import org.sonar.api.config.Configuration;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

/**
 * @author Evgeny Mandrikov
 */
public class LdapUserMapping {

  private static final Logger LOG = Loggers.get(LdapUserMapping.class);

  private static final String DEFAULT_OBJECT_CLASS = "inetOrgPerson";
  private static final String DEFAULT_LOGIN_ATTRIBUTE = "uid";
  private static final String DEFAULT_NAME_ATTRIBUTE = "cn";
  private static final String DEFAULT_EMAIL_ATTRIBUTE = "mail";
  private static final String DEFAULT_REQUEST = "(&(objectClass=inetOrgPerson)(uid={login}))";

  private final String baseDn;
  private final String request;
  private final String realNameAttribute;
  private final String emailAttribute;

  /**
   * Constructs mapping from Sonar settings.
   */
  public LdapUserMapping(Configuration settings, String settingsPrefix) {
    String usesrBaseDnSettingKey = settingsPrefix + ".user.baseDn";
    String usersBaseDn = settings.get(usesrBaseDnSettingKey).orElse(null);
    if (usersBaseDn == null) {
      String realm = settings.get(settingsPrefix + ".realm").orElse(null);
      if (realm != null) {
        LOG.warn("Auto-discovery feature is deprecated, please use '{}' to specify user search dn", usesrBaseDnSettingKey);
        usersBaseDn = LdapAutodiscovery.getDnsDomainDn(realm);
      }
    }

    String objectClass = settings.get(settingsPrefix + ".user.objectClass").orElse(null);
    String loginAttribute = settings.get(settingsPrefix + ".user.loginAttribute").orElse(null);

    this.baseDn = usersBaseDn;
    this.realNameAttribute = settings.get(settingsPrefix + ".user.realNameAttribute").orElse(DEFAULT_NAME_ATTRIBUTE);
    this.emailAttribute = settings.get(settingsPrefix + ".user.emailAttribute").orElse(DEFAULT_EMAIL_ATTRIBUTE);

    String req;
    if (StringUtils.isNotBlank(objectClass) || StringUtils.isNotBlank(loginAttribute)) {
      objectClass = StringUtils.defaultString(objectClass, DEFAULT_OBJECT_CLASS);
      loginAttribute = StringUtils.defaultString(loginAttribute, DEFAULT_LOGIN_ATTRIBUTE);
      req = "(&(objectClass=" + objectClass + ")(" + loginAttribute + "={login}))";
      // For backward compatibility with plugin versions lower than 1.2
      Loggers.get(LdapGroupMapping.class)
        .warn("Properties '{}.user.objectClass' and '{}.user.loginAttribute' are deprecated and should be " +
          "replaced by single property '{}.user.request' with value: {}",
          settingsPrefix, settingsPrefix, settingsPrefix, req);
    } else {
      req = settings.get(settingsPrefix + ".user.request").orElse(DEFAULT_REQUEST);
    }
    req = StringUtils.replace(req, "{login}", "{0}");
    this.request = req;
  }

  /**
   * Search for this mapping.
   */
  public LdapSearch createSearch(LdapContextFactory contextFactory, String username) {
    return new LdapSearch(contextFactory)
      .setBaseDn(getBaseDn())
      .setRequest(getRequest())
      .setParameters(username);
  }

  /**
   * Base DN. For example "ou=users,o=mycompany" or "cn=users" (Active Directory Server).
   */
  public String getBaseDn() {
    return baseDn;
  }

  /**
   * Request. For example:
   * <pre>
   * (&amp;(objectClass=inetOrgPerson)(uid={0}))
   * (&amp;(objectClass=user)(sAMAccountName={0}))
   * </pre>
   */
  public String getRequest() {
    return request;
  }

  /**
   * Real Name Attribute. For example "cn".
   */
  public String getRealNameAttribute() {
    return realNameAttribute;
  }

  /**
   * EMail Attribute. For example "mail".
   */
  public String getEmailAttribute() {
    return emailAttribute;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "{" +
      "baseDn=" + getBaseDn() +
      ", request=" + getRequest() +
      ", realNameAttribute=" + getRealNameAttribute() +
      ", emailAttribute=" + getEmailAttribute() +
      "}";
  }

}
