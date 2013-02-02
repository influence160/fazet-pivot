package com.ott.fazet.wtk;

import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.collections.List;
import org.apache.pivot.collections.immutable.ImmutableList;
import org.apache.pivot.wtk.Span;

public class SubstractableSpan {
	
	private Span span;
	
	public SubstractableSpan() {
	}

	public SubstractableSpan(Span span) {
		this.span = span.normalize();
	}
	
	public ImmutableList<Span> substract(Span span2) {
		Span intersection = this.span.intersect(span2);
		ArrayList<Span> substraction = new ArrayList<Span>();
		if (intersection == null) {
			substraction.add(this.span);
		} else {
			int maxStart = Math.max(this.span.start, intersection.start);
			int minStart = Math.min(this.span.start, intersection.start);
			int minEnd = Math.min(this.span.end, intersection.end);
			int maxEnd = Math.max(this.span.end, intersection.end);
			if (minStart == maxStart) {
				if (minEnd != maxEnd) {
					substraction.add(new Span(minEnd + 1, maxEnd));
				}
			} else {
				if (minEnd == maxEnd){
					substraction.add(new Span(minStart, maxStart - 1));
				} else {
					substraction.add(new Span(minStart, maxStart - 1));
					substraction.add(new Span(minEnd + 1, maxEnd));
				}
			}
		}
		return new ImmutableList<Span>(substraction);		
	}

	public Span getSpan() {
		return span;
	}

	public void setSpan(Span span) {
		this.span = span.normalize();
	}

}
