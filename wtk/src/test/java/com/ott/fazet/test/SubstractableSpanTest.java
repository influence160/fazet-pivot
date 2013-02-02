package com.ott.fazet.test;

import static junit.framework.Assert.*;

import org.apache.pivot.collections.immutable.ImmutableList;
import org.apache.pivot.wtk.Span;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.ott.fazet.wtk.SubstractableSpan;


public class SubstractableSpanTest {

    @Test
    @Category(SubstractableSpan.class)
    public void testIntersectedSpans(){
    	Span span1 = new Span(0, 5);
    	Span span2 = new Span(3, 7);
    	SubstractableSpan sub = new SubstractableSpan(span1);
    	ImmutableList<Span> substraction = sub.substract(span2);
    	assertEquals(1, substraction.getLength());
    	assertEquals(new Span(0, 2), substraction.get(0));
    }
    
    @Test
    @Category(SubstractableSpan.class)
    public void testNotIntersectedSpans(){
    	Span span1 = new Span(0, 5);
    	Span span2 = new Span(6, 8);
    	SubstractableSpan sub = new SubstractableSpan(span1);
    	ImmutableList<Span> substraction = sub.substract(span2);
    	assertEquals(1, substraction.getLength());
    	assertEquals(span1, substraction.get(0));
    }

    @Test
    @Category(SubstractableSpan.class)
    public void testInclusedSpans(){
    	Span span1 = new Span(0, 5);
    	Span span2 = new Span(2, 3);
    	SubstractableSpan sub = new SubstractableSpan(span1);
    	ImmutableList<Span> substraction = sub.substract(span2);
    	assertEquals(2, substraction.getLength());
    	assertEquals(new Span(0, 1), substraction.get(0));
    	assertEquals(new Span(4, 5), substraction.get(1));    	
    }

    @Test
    @Category(SubstractableSpan.class)
    public void testEqualsSpans(){
    	Span span1 = new Span(0, 5);
    	Span span2 = new Span(0, 5);
    	SubstractableSpan sub = new SubstractableSpan(span1);
    	ImmutableList<Span> substraction = sub.substract(span2);
    	assertEquals(0, substraction.getLength());   	
    }
    
}
