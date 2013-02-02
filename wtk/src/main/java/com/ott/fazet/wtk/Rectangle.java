package com.ott.fazet.wtk;

import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.collections.Dictionary;
import org.apache.pivot.collections.List;
import org.apache.pivot.collections.immutable.ImmutableList;
import org.apache.pivot.json.JSONSerializer;
import org.apache.pivot.serialization.SerializationException;
import org.apache.pivot.wtk.Point;
import org.apache.pivot.wtk.Span;

/**
 * Class representing a range of pair values. The range includes all
 * pairs in the interval <i>[[x.start, x.end], [y.start, y.end]]</i>
 */
public final class Rectangle {
    public final Span x;
    public final Span y;

    public static final String X_KEY = "x";
    public static final String Y_KEY = "y";

    public Rectangle(Point point) {
        x = new Span(point.x, point.x);
        y = new Span(point.y, point.y);
    }

    public Rectangle(Span x, Span y) {
        this.x = x;
        this.y = y;
    }

    public Rectangle(Rectangle rectangle) {
        if (rectangle == null) {
            throw new IllegalArgumentException("rectangle is null.");
        }

        x = rectangle.x;
        y = rectangle.y;
    }

    public Rectangle(Dictionary<String, ?> rectangle) {
        if (rectangle == null) {
            throw new IllegalArgumentException("rectangle is null.");
        }
        
        if (!rectangle.containsKey(X_KEY)) {
            throw new IllegalArgumentException(X_KEY + " is required.");
        }

        if (!rectangle.containsKey(Y_KEY)) {
            throw new IllegalArgumentException(Y_KEY + " is required.");
        }

        Dictionary<String, ?> xx = (Dictionary<String, ?>) rectangle.get(X_KEY);
        Dictionary<String, ?> yy = (Dictionary<String, ?>) rectangle.get(X_KEY);
        this.x = new Span(xx);
        this.y = new Span(yy);
    }

    /**
     * Returns the length of the span's x.
     *
     * @return
     * The absolute value of (<tt>end</tt> minus <tt>start</tt>) + 1.
     */
    public long getXLength() {
        return x.getLength();
    }
    
    /**
     * Returns the length of the span's y.
     *
     * @return
     * The absolute value of (<tt>end</tt> minus <tt>start</tt>) + 1.
     */
    public long getYLength() {
        return y.getLength();
    }
    
    /**
     * Returns the number of elements of the rectangle.
     *
     * @return
     * x.getLength() * y.getLength().
     */
    public long getSize() {
        return x.getLength() * y.getLength();
    }

    /**
     * Determines whether this rectangle contains another rectangle.
     *
     * @param rectangle
     * The rectangle to test for containment.
     *
     * @return
     * <tt>true</tt> if this rectangle contains <tt>rectangle</tt>; <tt>false</tt>,
     * otherwise.
     */
    public boolean contains(Rectangle rectangle) {
        if (rectangle == null) {
            throw new IllegalArgumentException("rectangle is null.");
        }

        //Rectangle normalizedRectangle = rectangle.normalize();
        //TODO verifier
        boolean contains = (x.contains(rectangle.x)
                && y.contains(rectangle.y));
        return contains;
    }

    /**
     * Determines whether this span intersects with another span.
     *
     * @param span
     * The span to test for intersection.
     *
     * @return
     * <tt>true</tt> if this span intersects with <tt>span</tt>;
     * <tt>false</tt>, otherwise.
     */
    public boolean intersects(Rectangle rectangle) {
        if (rectangle == null) {
            throw new IllegalArgumentException("rectangle is null.");
        }
        
        boolean intersects = (x.intersects(rectangle.x)
        		&& y.intersects(rectangle.y));
        return intersects;
    }

    /**
     * Calculates the intersection of this rectangle and another rectangle.
     *
     * @param rectangle
     * The rectangle to intersect with this rectangle.
     *
     * @return
     * A new Rectangle instance representing the intersection of this rectangle and
     * <tt>rectangle</tt>, or null if the rectangles do not intersect.
     */
    public Rectangle intersect(Rectangle rectangle) {
        if (rectangle == null) {
            throw new IllegalArgumentException("rectangle is null.");
        }

        Rectangle intersection = null;

        if (intersects(rectangle)) {
            intersection = new Rectangle(x.intersect(rectangle.x),
        		y.intersect(rectangle.y));
        }

        return intersection;
    }

    /**
     * Calculates the union of this rectangle and another rectangle.
     *
     * @param span
     * The span to union with this span.
     *
     * @return
     * A new Rectangle instance representing the union of this rectangle and
     * <tt>rectangle</tt>.
     */
    public Rectangle union(Rectangle rectangle) {
        if (rectangle == null) {
            throw new IllegalArgumentException("rectangle is null.");
        }

        return new Rectangle(x.union(rectangle.x),
    		y.union(rectangle.y));
    }
    
    /**
     * Calculates the rectangles result from substraction of the
     * intersection of this rectangle and one other rectangle from 
     * this rectangle.
     *
     * @param rectangle
     * The rectangle to substract from this rectangle.
     *
     * @return
     * A List of Rectangles representing the rest of the substraction
     * of a rectangle from this rectangle
     * <tt>rectangle</tt>.
     */
    public ImmutableList<Rectangle> substract(Rectangle rectangle) {
        if (rectangle == null) {
            throw new IllegalArgumentException("rectangle is null.");
        }
        
        Rectangle normalized = normalize();
        List<Rectangle> substraction = new ArrayList<Rectangle>();
        Rectangle intersection = intersect(rectangle);
        
        if(intersection == null){
        	substraction.add(normalized);
        } else {
        	Span xIntersection = normalized.x.intersect(intersection.x);
        	//Span yIntersection = normalized.y.intersect(intersection.y);
            SubstractableSpan substractableX = new SubstractableSpan(normalized.x);
            SubstractableSpan substractableY = new SubstractableSpan(normalized.y);
            ImmutableList<Span> xSubstraction = substractableX.substract(intersection.x);
            ImmutableList<Span> ySubstraction = substractableY.substract(intersection.y);
            
            for (int i=0; i<xSubstraction.getLength(); i++) {
            	substraction.add(new Rectangle(xSubstraction.get(i), normalized.y));
            }
            
            for (int j=0; j<ySubstraction.getLength(); j++) {
            	substraction.add(new Rectangle(xIntersection, ySubstraction.get(j)));
            }
        }
        
        return new ImmutableList<Rectangle>(substraction);
    }

    /**
     * Returns a normalized equivalent of the span in which
     * <tt>start</tt> is guaranteed to be less than end.
     */
    public Rectangle normalize() {
        return new Rectangle(x.normalize(), y.normalize());
    }

    @Override
    public boolean equals(Object o) {
        boolean equal = false;

        if (o instanceof Rectangle) {
        	Rectangle rectangle = (Rectangle)o;
            equal = (x.equals(rectangle.x)
                && y.equals(rectangle.y));
        }

        return equal;
    }

    @Override
    public int hashCode() {
        return x.hashCode() + y.hashCode();
    }

    @Override
    public String toString() {
        return ("{x: " + x + ", y: " + y + "}");
    }

    public static Rectangle decode(String value) {
        if (value == null) {
            throw new IllegalArgumentException();
        }

        Rectangle rectangle;
        if (value.startsWith("{")) {
            try {
            	rectangle = new Rectangle(JSONSerializer.parseMap(value));
            } catch (SerializationException exception) {
                throw new IllegalArgumentException(exception);
            }
        } 
        //TODO
        else {
//        	rectangle = new Span(Integer.parseInt(value));
            throw new IllegalArgumentException(value + " is not a valid Rectangle String");
        }

        return rectangle;
    }
}