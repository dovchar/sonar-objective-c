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
package org.sonar.plugins.objectivec.violations;

import java.io.File;
import java.util.Collection;

import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.Violation;
import org.sonar.plugins.objectivec.ObjectiveCPlugin;
import org.sonar.plugins.objectivec.core.ObjectiveC;

public final class OCLintSensor implements Sensor {
    public static final String REPORT_PATH_KEY = ObjectiveCPlugin.PROPERTY_PREFIX
            + ".oclint.report";
    public static final String DEFAULT_REPORT_PATH = "sonar-reports/oclint.xml";
    private final Settings conf;

    public OCLintSensor() {
        this(null);
    }

    public OCLintSensor(final Settings config) {
        conf = config;
    }

    public boolean shouldExecuteOnProject(final Project project) {
        return ObjectiveC.KEY.equals(project.getLanguageKey());
    }

    public void analyse(final Project project, final SensorContext context) {
        final String projectBaseDir = project.getFileSystem().getBasedir()
                .getPath();
        final OCLintParser parser = new OCLintParser(project, context);
        saveViolations(parseReportIn(projectBaseDir, parser), context);

    }

    private void saveViolations(final Collection<Violation> violations,
            final SensorContext context) {
        for (final Violation violation : violations) {
            context.saveViolation(violation);
        }
    }

    private Collection<Violation> parseReportIn(final String baseDir,
            final OCLintParser parser) {
        final StringBuilder reportFileName = new StringBuilder(baseDir);
        reportFileName.append("/").append(reportPath());

        LoggerFactory.getLogger(getClass()).info("Processing OCLint report {}",
                reportFileName);
        return parser.parseReport(new File(reportFileName.toString()));
    }

    private String reportPath() {
        String reportPath = conf.getString(REPORT_PATH_KEY);
        if (reportPath == null) {
            reportPath = DEFAULT_REPORT_PATH;
        }
        return reportPath;
    }
}
