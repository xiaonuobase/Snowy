/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package vip.xiaonuo.auth.core.protocol.saml.core;

import org.apache.commons.lang3.StringUtils;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml2.core.LogoutRequest;

import java.util.Objects;

public class LogoutRequestSchemaValidator {
  public void validate(LogoutRequest request) throws SamlException {
    validateID(request);
    validateVersion(request);
    validateIssueInstant(request);
    validateIdentifiers(request);
  }

  private void validateID(LogoutRequest request) throws SamlException {
    if (StringUtils.isEmpty(request.getID())) {
      throw new SamlException("ID attribute must not be empty");
    }
  }

  private void validateVersion(LogoutRequest request) throws SamlException {
    if (request.getVersion() == null) {
      throw new SamlException("Version attribute must not be null");
    }
    if (!Objects.equals(request.getVersion().toString(), SAMLVersion.VERSION_20.toString())) {
      throw new SamlException("Wrong SAML Version");
    }
  }

  private void validateIssueInstant(LogoutRequest request) throws SamlException {
    if (request.getIssueInstant() == null) {
      throw new SamlException("IssueInstant attribute must not be null");
    }
  }

  /**
   * Validate the Identifier child types (BaseID, NameID, EncryptedID).
   *
   * @param request the request being processed
   * @throws SamlException thrown if the identifiers present are not valid
   */
  protected void validateIdentifiers(LogoutRequest request) throws SamlException {
    int idCount = 0;

    if (request.getBaseID() != null) {
      idCount++;
    }
    if (request.getNameID() != null) {
      idCount++;
    }
    if (request.getEncryptedID() != null) {
      idCount++;
    }

    if (idCount != 1) {
      throw new SamlException(
          "LogoutRequest must contain exactly one of: BaseID, NameID, EncryptedID");
    }
  }
}
