/** Copyright (c) 2012 Memorial Sloan-Kettering Cancer Center.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 * documentation provided hereunder is on an "as is" basis, and
 * Memorial Sloan-Kettering Cancer Center 
 * has no obligations to provide maintenance, support,
 * updates, enhancements or modifications.  In no event shall
 * Memorial Sloan-Kettering Cancer Center
 * be liable to any party for direct, indirect, special,
 * incidental or consequential damages, including lost profits, arising
 * out of the use of this software and its documentation, even if
 * Memorial Sloan-Kettering Cancer Center 
 * has been advised of the possibility of such damage.
*/

// package
package org.mskcc.cbio.importer.converter.internal;

// imports
import org.mskcc.cbio.importer.Config;
import org.mskcc.cbio.importer.CaseIDs;
import org.mskcc.cbio.importer.IDMapper;
import org.mskcc.cbio.importer.Converter;
import org.mskcc.cbio.importer.FileUtils;
import org.mskcc.cbio.importer.model.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.regex.Matcher;

/**
 * Class which implements the Converter interface for use
 * with TCGA clinical data generated by the Biospecimen Core Resource.
 */
public class SampleClinicalDataConverterImpl extends ClinicalDataConverterImpl implements Converter
{
	private static Log LOG = LogFactory.getLog(SampleClinicalDataConverterImpl.class);

	public SampleClinicalDataConverterImpl(Config config, FileUtils fileUtils,
                                            CaseIDs caseIDs, IDMapper idMapper)
    {
        super(config, fileUtils, caseIDs, idMapper);
	}

	@Override
	public void createStagingFile(PortalMetadata portalMetadata, CancerStudyMetadata cancerStudyMetadata,
								  DatatypeMetadata datatypeMetadata, DataMatrix[] dataMatrices) throws Exception {

        DataMatrix sampleMatrix = dataMatrices[0];

        processSampleMatrix(cancerStudyMetadata, sampleMatrix);

		logMessage(LOG, "createStagingFile(), writing staging file.");
        fileUtils.writeStagingFile(portalMetadata.getStagingDirectory(), cancerStudyMetadata, datatypeMetadata, sampleMatrix);
		logMessage(LOG, "createStagingFile(), complete.");
	}

    private void processSampleMatrix(CancerStudyMetadata cancerStudyMetadata, DataMatrix sampleMatrix)
    {
        Map<String, ClinicalAttributesMetadata> clinicalAttributes = getClinicalAttributes(sampleMatrix.getColumnHeaders());

        config.flagMissingClinicalAttributes(cancerStudyMetadata.toString() + " (sample file)", cancerStudyMetadata.getTumorType(),
                                             removeUnknownColumnsFromMatrix(sampleMatrix, clinicalAttributes));

        // comes last - modifying original column headers
        modifyMatrixHeaderToPortalSpec(sampleMatrix, clinicalAttributes);
    }
}
