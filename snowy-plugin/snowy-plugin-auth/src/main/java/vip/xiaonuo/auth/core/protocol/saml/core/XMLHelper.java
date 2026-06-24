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

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.xml.BasicParserPool;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.w3c.dom.ls.LSSerializerFilter;

import java.io.Writer;
import java.util.Map;

public class XMLHelper {
  /**
   * Writes a Node out to a Writer using the DOM, level 3, Load/Save serializer. The written content is encoded using
   * the encoding specified in the writer configuration.
   *
   * @param node the node to write out
   * @param output the writer to write the XML to
   */
  public static void writeNode(Node node, Writer output) {
    writeNode(node, output, null);
  }

  /**
   * Writes a Node out to a Writer using the DOM, level 3, Load/Save serializer. The written content is encoded using
   * the encoding specified in the writer configuration.
   *
   * @param node the node to write out
   * @param output the writer to write the XML to
   * @param serializerParams parameters to pass to the {@link DOMConfiguration} of the serializer
   *         instance, obtained via {@link LSSerializer#getDomConfig()}. May be null.
   */
  public static void writeNode(Node node, Writer output, Map<String, Object> serializerParams) {
    DOMImplementationLS domImplLS = getLSDOMImpl(node);

    LSSerializer serializer = getLSSerializer(domImplLS, serializerParams);

    LSOutput serializerOut = domImplLS.createLSOutput();
    serializerOut.setCharacterStream(output);

    serializer.write(node, serializerOut);
  }

  /**
   * Obtain a the DOM, level 3, Load/Save serializer {@link LSSerializer} instance from the
   * given {@link DOMImplementationLS} instance.
   *
   * <p>
   * The serializer instance will be configured with the parameters passed as the <code>serializerParams</code>
   * argument. It will also be configured with an {@link LSSerializerFilter} that shows all nodes to the filter,
   * and accepts all nodes shown.
   * </p>
   *
   * @param domImplLS the DOM Level 3 Load/Save implementation to use
   * @param serializerParams parameters to pass to the {@link DOMConfiguration} of the serializer
   *         instance, obtained via {@link LSSerializer#getDomConfig()}. May be null.
   *
   * @return a new LSSerializer instance
   */
  public static LSSerializer getLSSerializer(
      DOMImplementationLS domImplLS, Map<String, Object> serializerParams) {
    LSSerializer serializer = domImplLS.createLSSerializer();

    serializer.setFilter(
        new LSSerializerFilter() {

          @Override
          public short acceptNode(Node arg0) {
            return FILTER_ACCEPT;
          }

          @Override
          public int getWhatToShow() {
            return SHOW_ALL;
          }
        });

    if (serializerParams != null) {
      DOMConfiguration serializerDOMConfig = serializer.getDomConfig();
      for (String key : serializerParams.keySet()) {
        serializerDOMConfig.setParameter(key, serializerParams.get(key));
      }
    }

    return serializer;
  }

  /**
   * Get the DOM Level 3 Load/Save {@link DOMImplementationLS} for the given node.
   *
   * @param node the node to evaluate
   * @return the DOMImplementationLS for the given node
   */
  public static DOMImplementationLS getLSDOMImpl(Node node) {
    DOMImplementation domImpl;
    if (node instanceof Document) {
      domImpl = ((Document) node).getImplementation();
    } else {
      domImpl = node.getOwnerDocument().getImplementation();
    }

    DOMImplementationLS domImplLS = (DOMImplementationLS) domImpl.getFeature("LS", "3.0");
    return domImplLS;
  }

  /**
   * Creates a DOM parser
   *
   * @return BasicParserPool
   *
   * @throws SamlException
   */
  public static BasicParserPool createDOMParser() throws SamlException {
    BasicParserPool basicParserPool = new BasicParserPool();
    try {
      basicParserPool.initialize();
    } catch (ComponentInitializationException e) {
      throw new SamlException("Failed to create an XML parser");
    }

    return basicParserPool;
  }
}
