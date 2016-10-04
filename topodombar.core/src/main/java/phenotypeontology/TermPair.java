/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phenotypeontology;

import ontologizer.go.Term;

/**
 * Implements a pair of two single terms with a score
 * @author jonas
 */
public class TermPair {
    
    private final Term pp;
    private final Term gp;
    private final Double s;
    
    /**
     * Constructor
     * @param pp
     * @param gp
     * @param s
     */
    public TermPair(Term pp, Term gp, Double s){
        
        this.pp = pp;
        this.gp = gp;
        this.s = s;
    }

    public Term getPp() {
        return pp;
    }

    public Term getGp() {
        return gp;
    }


    public Double getS() {
        return s;
    }
    
    
}