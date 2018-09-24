package wibd.ls.ml.util;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.amazonaws.services.rekognition.model.BoundingBox;
import com.amazonaws.services.rekognition.model.Geometry;
import com.amazonaws.services.rekognition.model.Point;
import com.amazonaws.services.rekognition.model.TextDetection;

public class TextExtraction {

	private int yrPosition;

	private BoundingBox getMinTextTopPosition(List<TextDetection> textDetections) {
		BoundingBox minTopBox = null;
		Geometry geo = null;
		for (int i = 0; i < textDetections.size(); i++) {
			TextDetection textDetection = textDetections.get(i);
			geo = textDetection.getGeometry();
			BoundingBox bbox = geo.getBoundingBox();
			if (minTopBox == null) {
				minTopBox = bbox;
			} else {
				if (minTopBox != null && minTopBox.getTop() > bbox.getTop() && minTopBox.getLeft() > bbox.getLeft()) {
					minTopBox = bbox;
				}
			}
			System.out.println("TextExtractionUtil.getMinTextTopPosition() --- TOP: " + bbox.getTop());
			String extractedText = textDetection.getDetectedText();
			System.out.println("extracted text is " + extractedText);
		}
		System.out.println("TextExtractionUtil.getMinTextTopPosition() --- MIN TOP: " + minTopBox.getTop()
				+ " and height is " + minTopBox.getHeight());
		return minTopBox;
	}

	public boolean overlaps(BoundingBox r, BoundingBox s) {
		System.out.println("TextExtraction.overlaps() r is "+r.toString());
		System.out.println("TextExtraction.overlaps() s is "+s.toString());
	    double xPosLow = r.getLeft();
		double xPosHigh = r.getLeft() + r.getWidth();
		double yposLow = r.getTop();
		double yPosHigh = r.getTop() + r.getHeight();
		
		System.out.println("x position range "+xPosLow +" - "+xPosHigh);
		System.out.println("y position range "+yposLow+" - "+yPosHigh);
		
		boolean topLeft = (s.getLeft() >= xPosLow || s.getLeft()<= xPosHigh) && (s.getTop() >= yposLow || s.getTop() <= yPosHigh);
		boolean topRight = ((s.getLeft() + s.getWidth())>= xPosLow || (s.getLeft() + s.getWidth())<= xPosHigh) && (s.getTop() >= yposLow || s.getTop() <= yPosHigh);
		boolean bottomLeft = (s.getLeft() >= xPosLow || s.getLeft()<= xPosHigh) && ((s.getTop() + s.getHeight())>= yposLow || (s.getTop() + s.getHeight()) <= yPosHigh);
		boolean bottomRight = ((s.getLeft() + s.getWidth()) >= xPosLow || (s.getLeft() + s.getWidth())<= xPosHigh) && ((s.getTop() + s.getHeight()) >= yposLow || (s.getTop() + s.getHeight()) <= yPosHigh);
		
		return topLeft || topRight || bottomLeft || bottomRight;
	}

	public TreeMap<String, List<Integer>> computeTextpositionMap(List<TextDetection> textDetections) {
		BoundingBox minTopBox = getMinTextTopPosition(textDetections);
		TreeMap<String, List<Integer>> textpositionMap = new TreeMap<>(Collections.reverseOrder());
		for (int i = 0; i < textDetections.size(); i++) {// && numList.size() < 2
			TextDetection textDetection = textDetections.get(i);
			Geometry geo = textDetection.getGeometry();
			BoundingBox bbox = geo.getBoundingBox();
			List<Point> points = geo.getPolygon();
			for(Point pt : points) {
				System.out.println(pt.getX()+":"+pt.getY());
			}
			String extractedText = textDetection.getDetectedText();
			System.out.println("extracted text is " + extractedText);
			if (overlaps(minTopBox, bbox)) {
				System.out.println("TOP: " + bbox.getTop());
				Charset charset = Charset.forName("UTF-8");
				extractedText = charset.decode(charset.encode(extractedText)).toString();
				String cleanedText = extractedText.replaceAll("[^0-9 ]", "").replaceAll("\\.", "");
				System.out.println("cleaned Text is " + cleanedText);
				String[] textArr = cleanedText.split(" ");
				for (String str : textArr) {
					System.out.println(str);
					try {
						Integer num = Integer.valueOf(str);
						String key = String.valueOf(str.length());
						if (num != null) {
							List<Integer> val = null;
							if (textpositionMap.containsKey(key)) {
								val = textpositionMap.get(key);
							} else {
								val = new ArrayList<>();
							}
							val.add(num);
							textpositionMap.put(key, val);
							System.out.println("added to map at " + key + " and value is " + num);
						}
					} catch (NumberFormatException e) {
					}
				}
			}
		}
		return textpositionMap;
	}

	public Integer getYear(TreeMap<String, List<Integer>> textpositionMap) {
		yrPosition = 0;
		String yrKey = "4";
		if (textpositionMap != null && textpositionMap.containsKey(yrKey)) {
			List<Integer> yearList = textpositionMap.get(yrKey);
			if (yearList != null && yearList.size() != 0) {
				for (int i = 0; i < yearList.size(); i++) {
					if (!(Calendar.getInstance().get(Calendar.YEAR) < yearList.get(i))) {
						yrPosition = i;
						return yearList.get(i);
					}
				}
			}
		}
		return null;
	}

	public Integer getPatent(TreeMap<String, List<Integer>> textpositionMap) {
		if (textpositionMap != null) {
			Iterator<Entry<String, List<Integer>>> iter = textpositionMap.entrySet().iterator();
			if (iter.hasNext()) {
				Entry<String, List<Integer>> entry = iter.next();
				String key = entry.getKey();
				List<Integer> patList = entry.getValue();
				if (key.equals("4")) {
					if (patList != null && patList.size() > 1) {
						for (int i = 0; i < patList.size(); i++) {
							if (i != yrPosition) {
								System.out.println("key is " + key + " and patent is " + patList.get(i));
								return patList.get(i);
							}
						}

					} else {
						if (iter.hasNext()) {
							entry = iter.next();
							key = entry.getKey();
							patList = entry.getValue();
						}
					}
				}
				System.out.println("key is " + key + " and patent is " + patList.get(0));
				return patList != null && patList.size() > 0 ? patList.get(0) : null;
			}
		}
		return null;
	}

}
