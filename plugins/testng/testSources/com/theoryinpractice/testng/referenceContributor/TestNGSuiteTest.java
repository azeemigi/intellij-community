/*
 * Copyright 2000-2011 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.theoryinpractice.testng.referenceContributor;

import com.intellij.openapi.application.PluginPathManager;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.intellij.util.ArrayUtil;
import com.intellij.util.ui.UIUtil;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * User: anna
 * Date: Sep 3, 2010
 */
@Test
public class TestNGSuiteTest extends LightCodeInsightFixtureTestCase {
  @BeforeMethod
  @Override
  protected void setUp() throws Exception {
    UIUtil.invokeAndWaitIfNeeded(new Runnable() {
      @Override
      public void run() {
        try {
          TestNGSuiteTest.super.setUp();
        }
        catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    });
  }

  @AfterMethod
  @Override
  protected void tearDown() throws Exception {
    UIUtil.invokeAndWaitIfNeeded(new Runnable() {
      @Override
      public void run() {
        try {
          TestNGSuiteTest.super.tearDown();
        }
        catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    });
  }

  public void testNothing(){}

  public void testTestNGSuiteFile() throws Throwable {
    UIUtil.invokeAndWaitIfNeeded(new Runnable() {
      @Override
      public void run() {
        try {
          myFixture.addClass("package org.testng.annotations; public @interface DataProvider {}");
          myFixture.addClass("package org.testng.annotations; public @interface Test {}");
          myFixture.addClass("package o; @Test public class MyTest { public void testMe(){} }");
          myFixture.addFileToProject("subPack/test-unit.xml", "<suite>" +
                                                  "<test>" +
                                                    "<classes></classes>" +
                                                  "</test>" +
                                                "</suite>");
          myFixture.testHighlighting("testng.xml");
        }
        catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    });
  }

  @Override
  protected String getBasePath() {
    return PluginPathManager.getPluginHomePathRelative("testng") + "/testData/references";
  }


}
