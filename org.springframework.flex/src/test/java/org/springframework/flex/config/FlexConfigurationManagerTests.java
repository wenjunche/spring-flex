/*
 * Copyright 2002-2009 the original author or authors.
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

package org.springframework.flex.config;

import javax.servlet.ServletConfig;

import junit.framework.TestCase;

import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.web.context.support.StaticWebApplicationContext;

import flex.messaging.config.ConfigurationManager;
import flex.messaging.config.MessagingConfiguration;

public class FlexConfigurationManagerTests extends TestCase {

    ServletConfig config = new MockServletConfig();

    StaticWebApplicationContext context = new StaticWebApplicationContext();

    ConfigurationManager configManager;

    @Override
    public void setUp() {
        this.context.setServletConfig(this.config);
    }

    public void testCustomConfiguration() {
        this.context.registerSingleton("configParser", flex.messaging.config.XPathServerConfigurationParser.class);
        RuntimeBeanReference parserReference = new RuntimeBeanReference("configParser");
        GenericBeanDefinition configManagerDef = new GenericBeanDefinition();
        configManagerDef.setBeanClass(FlexConfigurationManager.class);
        configManagerDef.getPropertyValues().addPropertyValue("configurationParser", parserReference);
        configManagerDef.getPropertyValues().addPropertyValue("configurationPath", "classpath:org/springframework/flex/core/services-config.xml");
        this.context.getDefaultListableBeanFactory().registerBeanDefinition("configurationManager", configManagerDef);
        this.context.refresh();

        this.configManager = (ConfigurationManager) this.context.getBean("configurationManager");

        MessagingConfiguration messagingConfiguration = this.configManager.getMessagingConfiguration(this.config);

        assertNotNull(messagingConfiguration);
        assertNotNull(messagingConfiguration.getServiceSettings("message-service"));
        assertNotNull(messagingConfiguration.getServiceSettings("proxy-service"));
        assertNotNull(messagingConfiguration.getServiceSettings("remoting-service"));
    }

    public void testGetMessagingConfiguration() {
        this.configManager = new FlexConfigurationManager(this.context, "classpath:org/springframework/flex/core/services-config.xml");

        MessagingConfiguration messagingConfiguration = this.configManager.getMessagingConfiguration(this.config);

        assertNotNull(messagingConfiguration);
        assertNotNull(messagingConfiguration.getServiceSettings("message-service"));
        assertNotNull(messagingConfiguration.getServiceSettings("proxy-service"));
        assertNotNull(messagingConfiguration.getServiceSettings("remoting-service"));
    }

    public void testGetMessagingConfiguration_NullServletConfig() {
        this.configManager = new FlexConfigurationManager(this.context, "classpath:org/springframework/flex/core/services-config.xml");

        try {
            this.configManager.getMessagingConfiguration(null);
            fail();
        } catch (Exception ex) {
            // expected
        }
    }
}