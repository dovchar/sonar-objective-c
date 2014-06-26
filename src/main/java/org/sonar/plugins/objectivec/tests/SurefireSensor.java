/*
 * Sonar Objective-C Plugin
 * Copyright (C) 2012 OCTO Technology
 * dev@sonar.codehaus.org
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
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.objectivec.tests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.CoverageExtension;
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.resources.Resource;
import org.sonar.plugins.objectivec.core.ObjectiveC;
import org.sonar.plugins.surefire.api.AbstractSurefireParser;
import org.sonar.plugins.surefire.api.SurefireUtils;

import java.io.File;

public class SurefireSensor implements Sensor {

  private static final Logger LOG = LoggerFactory.getLogger(SurefireSensor.class);

  @DependsUpon
  public Class<?> dependsUponCoverageSensors() {
    return CoverageExtension.class;
  }

  public boolean shouldExecuteOnProject(Project project) {
      return ObjectiveC.KEY.equals(project.getLanguageKey());
  }

  public void analyse(Project project, SensorContext context) {
    File dir = SurefireUtils.getReportsDirectory(project);
    collect(project, context, dir);
  }

  protected void collect(Project project, SensorContext context, File reportsDir) {
    LOG.info("parsing {}", reportsDir);
    SUREFIRE_PARSER.collect(project, context, reportsDir);
  }

  private static final AbstractSurefireParser SUREFIRE_PARSER = new AbstractSurefireParser() {
    @Override
    protected Resource<?> getUnitTestResource(String classKey) {
      String filename = classKey.replace('.', '/') + ".m";
      org.sonar.api.resources.File sonarFile = new org.sonar.api.resources.File(filename);
      sonarFile.setQualifier(Qualifiers.UNIT_TEST_FILE);
      return sonarFile;
    }
  };

  @Override
  public String toString() {
    return "Objective-C SurefireSensor";
  }

}