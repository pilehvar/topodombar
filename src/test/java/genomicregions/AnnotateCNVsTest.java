/*
 * Copyright (c) 2014, Jonas Ibn-Salem <ibnsalem@molgen.mpg.de>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package genomicregions;

import io.TabFileParser;
import io.TabFileParserTest;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import phenotypeontology.OntologyWrapper;
import toyexampledata.ExampleData;

/**
 *
 * @author Jonas Ibn-Salem <ibnsalem@molgen.mpg.de>
 */
public class AnnotateCNVsTest {
    
    // declare some variables to be knwon in this test
    private static GenomicSet<CNV> cnvs; 
    private static GenomicSet<CNV> exampleCNVs; 
    private static GenomicSet<GenomicElement> domains;
    private static GenomicSet<GenomicElement> boundaries;
    //private static GenomicElementSet<GenGenomicSet  public AnnotateCNVsTest() {
    private static GenomicSet<Gene> genes;
    
    private static ExampleData exampleData;
    
    @BeforeClass
    public static void setUpClass() throws IOException {

        // read sample CNVs:
        String cnvPath = TabFileParserTest.class.getResource("/sample_CNV_chr22.tab").getPath();
        TabFileParser cnvParser = new TabFileParser(cnvPath);
        cnvs = cnvParser.parseCNV();

        // read sample boundary elements
        String boundaryPath = TabFileParserTest.class.getResource("/sample_boundary_chr22.tab.addOne").getPath();
        TabFileParser boundaryParser = new TabFileParser(boundaryPath);
        boundaries = boundaryParser.parse();

        // read sample genes elements
        String genePath = TabFileParserTest.class.getResource("/sample_genes_chr22.tab").getPath();
        TabFileParser geneParser = new TabFileParser(genePath);
        genes = geneParser.parseGene();

        // parse toy example data set
        exampleData = new ExampleData();
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of boundaryOverlap method, of class AnnotateCNVs.
     */
    @Test
    public void testBoundaryOverlap() {
        System.out.println("boundaryOverlap");
        
        //System.out.println(cnvs);
        //System.out.println(boundaries);
        
        AnnotateCNVs.boundaryOverlap(cnvs, boundaries);
        
        // check if all boundaries are read.
        // The sample file containes 30 elements
        assertEquals(boundaries.size(), 30);
        
        AnnotateCNVs.boundaryOverlap(cnvs, boundaries);
        
        // count number of cnvs that overlap any boundary
        Integer cnt = 0;
        for (CNV c : cnvs.values()){
            if (! c.getBoundaryOverlap().isEmpty()){
                cnt++;
            }
        }
        // 34 of the sample CNVs should overlap completely at leaste one boundary, 
        // as calulated with the python script 'filter_overlap.py' from the barrier project.
        Integer expCnt = 34;
        assertEquals(expCnt, cnt);
    }

    /**
     * Test of geneOverlap method, of class AnnotateCNVs.
     */
    @Test
    public void testGeneOverlap() {
        System.out.println("geneOverlap");
        AnnotateCNVs.geneOverlap(cnvs, genes);
        
    }

    /**
     * Test of defineAdjacentRegionsByDomains method, of class AnnotateCNVs.
     */
    @Test
    public void testDefineAdjacentRegionsByDomains() {
        System.out.println("defineAdjacentRegionsByDomains");
        
        GenomicSet<CNV> cnvs = exampleData.getCnvs();
        GenomicSet<GenomicElement> domains = exampleData.getDomains();
        
        AnnotateCNVs.defineAdjacentRegionsByDomains(cnvs, domains);
        
        CNV cnv1 = cnvs.get("cnv1");
        GenomicElement expLeftRegion = new GenomicElement("chr1", 0, 9, "leftAdjacentRegion");
        GenomicElement expRightRegion = new GenomicElement("chr1", 19, 37, "rightAdjacentRegion");
        System.out.println("left exp: " + expLeftRegion);
        System.out.println("left act: " + cnv1.getLeftAdjacentRegion());
        System.out.println("equals? " + expLeftRegion.equals(cnv1.getLeftAdjacentRegion()));
        assertTrue(expLeftRegion.equals(cnv1.getLeftAdjacentRegion()));

        System.out.println("right exp: " + expRightRegion);
        System.out.println("right act: " + cnv1.getRightAdjacentRegion());
        System.out.println("equals? " + expRightRegion.equals(cnv1.getRightAdjacentRegion()));
        assertTrue(expRightRegion.equals(cnv1.getRightAdjacentRegion()));
    
    }


    /**
     * Test of phenogramScore method, of class AnnotateCNVs.
     */
    @Test
    public void testPhenogramScore() {
        
        System.out.println("phenogramScore");
        GenomicSet<CNV> cnvs = exampleData.getCnvs();
        OntologyWrapper ontolgyWrapper = exampleData.getOntologyWrapper();
        GenomicSet<Gene> exampleGenes = exampleData.getGenes();
        AnnotateCNVs.geneOverlap(cnvs, exampleGenes);
        AnnotateCNVs.phenogramScore(cnvs, ontolgyWrapper);
        
        Double expCnv1PhenoScore = 0.29; // see table in {@link ExampleData} doc
        Double expCnv2PhenoScore = 1.68; // see table in {@link ExampleData} doc

        assertEquals(expCnv1PhenoScore, cnvs.get("cnv1").getOverlapPhenogramScore(), 0.01);
        assertEquals(expCnv2PhenoScore, cnvs.get("cnv2").getOverlapPhenogramScore(), 0.01);
        
    }


    /**
     * Test of phenogramScoreAdjacentGenes method, of class AnnotateCNVs.
     */
    @Test
    public void testPhenogramScoreAdjacentGenes() {
        System.out.println("phenogramScoreAdjacentGenes");
        GenomicSet<CNV> cnvs = exampleData.getCnvs();
        GenomicSet<Gene> genes = exampleData.getGenes();
        GenomicSet<GenomicElement> domains = exampleData.getDomains();
        OntologyWrapper ontologyWrapper = exampleData.getOntologyWrapper();

        AnnotateCNVs.defineAdjacentRegionsByDomains(cnvs, domains);
        
        AnnotateCNVs.phenogramScoreAdjacentGenes(cnvs, genes, ontologyWrapper);
        for (CNV cnv : cnvs.values()){
            System.out.println("DEBUG: " + cnv + cnv.getLeftAdjacentRegion() + cnv.getLeftAdjacentPhenogramScore().toString());
        }
        CNV cnv1 = cnvs.get("cnv1");
        Double leftScore = cnv1.getLeftAdjacentPhenogramScore();
        assertEquals(0.0, leftScore, 0.01);

        assertEquals(1.68, cnv1.getRightAdjacentPhenogramScore(), 0.01);

        // cnv4 should have left score 0
        assertEquals(0.0, cnvs.get("cnv4").getLeftAdjacentPhenogramScore(), 0.01);
        
    }

    /**
     * Test of annotateTDBD method, of class AnnotateCNVs.
     */
    @Test
    public void testAnnotateTDBD() {
        System.out.println("annotateTDBD");
        
        GenomicSet<CNV> cnvs = exampleData.getCnvs();
        GenomicSet<Gene> genes = exampleData.getGenes();
        GenomicSet<GenomicElement> enhancer = exampleData.getEnhancer();
        GenomicSet<GenomicElement> boundaries = exampleData.getEnhancer();
        GenomicSet<GenomicElement> domains = exampleData.getDomains();
        OntologyWrapper ontologyWrapper = exampleData.getOntologyWrapper();

          
        AnnotateCNVs.boundaryOverlap(cnvs, boundaries);
        AnnotateCNVs.defineAdjacentRegionsByDomains(cnvs, domains);
        AnnotateCNVs.phenogramScoreAdjacentGenes(cnvs, genes, ontologyWrapper);

        AnnotateCNVs.annotateTDBD(cnvs, enhancer);
        
        assertTrue(cnvs.get("cnv1").isTDBD());
        assertFalse(cnvs.get("cnv2").isTDBD());
        assertFalse(cnvs.get("cnv3").isTDBD());
        assertFalse(cnvs.get("cnv4").isTDBD());
        
    }
    
}