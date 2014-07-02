/*
 * Copyright (C) 2014 Jonas Ibn-Salem <ibnsalem@molgen.mpg.de>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package genomicregions;

import jannovar.interval.Interval;



/**
 * Implements a genomic element or genomic interval.
 * 
 * @author Jonas Ibn-Salem <ibnsalem@molgen.mpg.de>
 */
public class GenomicElement implements Comparable{
    
    // Genomic location in zero-based half-open BED-like format:
    private String chr;
    private int start;
    private int end;
    private String name;    
    
    
    /**
     * Constructor for the {@link GenomicInterval} class.
     * 
     * @param chr   Chromosome identifier
     * @param start the zero-based start coordinate
     * @param end   the end coordinate (zero-based half open, BED-like format)
     * @param name  a name for this genomic element/interval
     * @throws IllegalArgumentException     in case of negative coordinates or if end coordinate is smaller or equal start coordinate
     */
    public GenomicElement(String chr, int start, int end, String name)  throws IllegalArgumentException {
        
        this.chr = chr;
        this.name = name;
        
        // test for negative coordinates
        if (start < 0 | end < 0 ){

            throw new IllegalArgumentException(
                    "GenomicInterval constructor: start and end coordinates have to be positive" 
                    + start + " end=" + end 
                );
        }
        
        // test for proper start coordinates
        if ( start >= end ){

            throw new IllegalArgumentException(
                    "GenomicInterval constructor: Start coordinate has to be "
                            + "smaller than end coordintae. start=" 
                            + start + " end=" + end 
                );
        }
        
        
        this.start = start;
        this.end = end;
        
    }

    
    /**
     * Convert element to {@link String} representation in the format "name|chr:start-(end-1)"
     * 
     * @return String representation of the genomic interval object 
     */
    @Override
    public String toString(){
        return name + ":" + chr + ":[" + start + "," + end + ")";
    }
    
    
    /**
     * Convert {@link GenomicElement} into an {@link Interval} object.
     * Since the {@link Interval} and {@link IntervalTree} classes from the 
     * {@link jannovar} project is implemented for 1-based coordinates and 
     * assumes closed intervals (end position included), we subtracted 1 from 
     * the end coordinate.
     * 
     * @return {@link Interval} object for the {@link GenomicElement}
     */
    public Interval toInterval(){
        return new Interval(start, end-1, this);
    }
    
    /**
     * Test if another {@link GenomicElement} object is equal to this
     * 
     * @param other Another {@link GenomicElement}
     * @return true if other {@link GenomicElement} object is equal 
     */
    public boolean equals(GenomicElement other){
        return other.toString().equals(this.toString());
    }
    
    /**
     * Test if the another {@link GenomicElement} has any overlap with this element.
     * 
     * @param other  An {@link GenomicElement} object that is tested for overlap
     * @return ture if any overlap else false
     */
    public boolean hasOverlap(GenomicElement other){
        
        // check if chromsomses for the two elements are equal
        boolean chrEquals = chr.equals(other.getChr());
        
        // check for any overlap: s1 < e2 and s2 < e1
        boolean overlap = (start < other.getEnd()) & (other.getStart() < end);
        
        return chrEquals &  overlap ;
    }

    
    /**
     * @return the chr
     */
    public String getChr() {
        return chr;
    }

    /**
     * @return the start
     */
    public int getStart() {
        return start;
    }

    /**
     * @return the end
     */
    public int getEnd() {
        return end;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Compares this {@link GenomicElement} with the specified {@link GenomicElement} for order. 
     * Returns a negative integer, zero, or a positive integer as this object 
     * is less than, equal to, or greater than the specified object.
     * 
     * @param o the {@link GenomicElement} to be compared. 
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object. 
     */
    @Override
    public int compareTo(Object o) {
        return toString().compareTo(o.toString());
    }
    
}
