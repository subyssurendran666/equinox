/*******************************************************************************
 * Copyright (c) 2004, 2013 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.osgi.tests.resolver;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.osgi.service.resolver.ExportPackageDescription;
import org.eclipse.osgi.service.resolver.State;
import org.eclipse.osgi.service.resolver.StateObjectFactory;
import org.eclipse.osgi.tests.services.resolver.AbstractStateTest;
import org.junit.Test;
import org.osgi.framework.BundleException;

public class TestCycle_004 extends AbstractStateTest {

	BundleDescription bundle_1 = null;
	BundleDescription bundle_2 = null;

	@Test
	public void testTest_001() {
		State state = buildEmptyState();
		StateObjectFactory sof = StateObjectFactory.defaultFactory;

		bundle_1 = create_bundle_1(sof);
		bundle_2 = create_bundle_2(sof);
		// ***************************************************
		// stage a
		// expect to pass =true
		// ***************************************************
		addBundlesToState_a(state);
		// ***************************************************
		try {
			state.resolve();
		} catch (Throwable t) {
			fail("unexpected exception class=" + t.getClass().getName() + " message=" + t.getMessage());
			return;
		}
		checkBundlesResolved_a();
		checkWiring_a();
	} // end of method

	public void checkWiringState_1() {
		ExportPackageDescription[] exports = bundle_1.getResolvedImports();
		assertNotNull("export array is unexpectedly null", exports);
		assertTrue("export array is unexpectedly empty", exports.length > 0);
		for (ExportPackageDescription exp : exports) {
			String exportPackageName = exp.getName();
			assertNotNull("package name is null", exportPackageName);
			if (exportPackageName.equals("p")) {
				assertNotNull("Package [p] is not wired when it should be ", exp.getExporter());
				assertEquals("Package [p] is wired incorrectly ", exp.getExporter(), bundle_2);
			}
		} // end for
	} // end method

	public void checkWiringState_2() {
		ExportPackageDescription[] exports = bundle_2.getResolvedImports();
		assertNotNull("export array is unexpectedly null", exports);
		assertTrue("export array is unexpectedly empty", exports.length > 0);
		for (ExportPackageDescription exp : exports) {
			String exportPackageName = exp.getName();
			assertNotNull("package name is null", exportPackageName);
			if (exportPackageName.equals("q")) {
				assertNotNull("Package [q] is not wired when it should be ", exp.getExporter());
				assertEquals("Package [q] is wired incorrectly ", exp.getExporter(), bundle_1);
			}
		} // end for
	} // end method

	public void checkWiring_a() {
		checkWiringState_1();
		checkWiringState_2();
	} // end method

	public void addBundlesToState_a(State state) {
		boolean added = false;
		added = state.addBundle(bundle_1);
		assertTrue("failed to add bundle ", added);
		added = state.addBundle(bundle_2);
		assertTrue("failed to add bundle ", added);
	} // end method

	public void checkBundlesResolved_a() {
		assertTrue("unexpected bundle resolution state", bundle_1.isResolved());
		assertTrue("unexpected bundle resolution state", bundle_2.isResolved());
	} // end method

	public BundleDescription create_bundle_1(StateObjectFactory sof) {
		java.util.Dictionary dictionary_1 = new java.util.Properties();
		BundleDescription bundle = null;
		dictionary_1.put("Bundle-ManifestVersion", "2");
		dictionary_1.put("Bundle-SymbolicName", "A");
		dictionary_1.put("Export-Package", "q; version=1.0");
		dictionary_1.put("Import-Package", "p; version=1.0");
		try {
			bundle = sof.createBundleDescription(dictionary_1, "bundle_1", 1);
		} catch (BundleException be) {
			fail(be.getMessage());
		}
		return bundle;
	} // end of method

	public BundleDescription create_bundle_2(StateObjectFactory sof) {
		java.util.Dictionary dictionary_2 = new java.util.Properties();
		BundleDescription bundle = null;
		dictionary_2.put("Bundle-ManifestVersion", "2");
		dictionary_2.put("Bundle-SymbolicName", "B");
		dictionary_2.put("Export-Package", "p; version=1.0");
		dictionary_2.put("Import-Package", "q; version=1.0");
		try {
			bundle = sof.createBundleDescription(dictionary_2, "bundle_2", 2);
		} catch (BundleException be) {
			fail(be.getMessage());
		}
		return bundle;
	} // end of method

} // end of testcase
