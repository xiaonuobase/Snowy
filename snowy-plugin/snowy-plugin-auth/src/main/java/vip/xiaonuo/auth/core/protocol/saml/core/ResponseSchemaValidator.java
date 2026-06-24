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
import org.opensaml.saml.saml2.core.StatusResponseType;

import java.util.Objects;

public class ResponseSchemaValidator {
  public void validate(StatusResponseType response) throws SamlException {
    validateStatus(response);
    validateID(response);
    validateVersion(response);
    validateIssueInstant(response);
  }

  private void validateStatus(StatusResponseType response) throws SamlException {
    if (response.getStatus() == null) {
      throw new SamlException("Status is required");
    }
  }

  private void validateID(StatusResponseType response) throws SamlException {
    if (StringUtils.isEmpty(response.getID())) {
      throw new SamlException("ID attribute must not be empty");
    }
  }

  private void validateVersion(StatusResponseType response) throws SamlException {
    if (response.getVersion() == null) {
      throw new SamlException("Version attribute must not be null");
    }
    if (!Objects.equals(response.getVersion().toString(), SAMLVersion.VERSION_20.toString())) {
      throw new SamlException("Wrong SAML Version");
    }
  }

  private void validateIssueInstant(StatusResponseType response) throws SamlException {
    if (response.getIssueInstant() == null) {
      throw new SamlException("IssueInstant attribute must not be null");
    }
  }
}
