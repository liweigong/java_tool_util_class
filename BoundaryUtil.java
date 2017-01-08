package com.ichinait.car.utils;
import java.util.ArrayList;
import java.util.List;

/**
 * 查看该点是否在指定范围内
 * 
 */
public class BoundaryUtil {
	
	/**
	 * {@link #isPointInPolygon(double, double, List, List)}
	 * @param px
	 * @param py
	 * @param polygon
	 * @return
	 */
	public static boolean isPointInPolygon(double px, double py, List<CoordPair> polygon) {
		List<Double> polygonXA = new ArrayList<Double>();
		List<Double> polygonYA = new ArrayList<Double>();
		
		for (CoordPair pair : polygon) {
			polygonXA.add(pair.getX());
			polygonYA.add(pair.getY());
		}
		
		return isPointInPolygon(px, py, polygonXA, polygonYA);
	}
	
	/**
	 * 查看该点是否在指定范围内
	 * @param px 待检测点x坐标
	 * @param py 待检测点y坐标
	 * @param polygonXA	x坐标列表
	 * @param polygonYA	y坐标列表
	 * @return true 在	false 不在 
	 */
	public static boolean isPointInPolygon(double px, double py,
			List<Double> polygonXA, List<Double> polygonYA) {
		boolean isInside = false;
		double ESP = 1e-9;
		int count = 0;
		double linePoint1x;
		double linePoint1y;
		double linePoint2x = 180;
		double linePoint2y;

		linePoint1x = px;
		linePoint1y = py;
		linePoint2y = py;

		for (int i = 0; i < polygonXA.size() - 1; i++) {
			double cx1 = polygonXA.get(i);
			double cy1 = polygonYA.get(i);
			double cx2 = polygonXA.get(i + 1);
			double cy2 = polygonYA.get(i + 1);
			if (isPointOnLine(px, py, cx1, cy1, cx2, cy2)) {
				return true;
			}
			if (Math.abs(cy2 - cy1) < ESP) {
				continue;
			}

			if (isPointOnLine(cx1, cy1, linePoint1x, linePoint1y, linePoint2x,
					linePoint2y)) {
				if (cy1 > cy2)
					count++;
			} else if (isPointOnLine(cx2, cy2, linePoint1x, linePoint1y,
					linePoint2x, linePoint2y)) {
				if (cy2 > cy1)
					count++;
			} else if (isIntersect(cx1, cy1, cx2, cy2, linePoint1x,
					linePoint1y, linePoint2x, linePoint2y)) {
				count++;
			}
		}
		if (count % 2 == 1) {
			isInside = true;
		}

		return isInside;
	}

	private static double Multiply(double px0, double py0, double px1, double py1,
			double px2, double py2) {
		return ((px1 - px0) * (py2 - py0) - (px2 - px0) * (py1 - py0));
	}

	private static boolean isPointOnLine(double px0, double py0, double px1,
			double py1, double px2, double py2) {
		boolean flag = false;
		double ESP = 1e-9;
		if ((Math.abs(Multiply(px0, py0, px1, py1, px2, py2)) < ESP)
				&& ((px0 - px1) * (px0 - px2) <= 0)
				&& ((py0 - py1) * (py0 - py2) <= 0)) {
			flag = true;
		}
		return flag;
	}

	private static boolean isIntersect(double px1, double py1, double px2, double py2,
			double px3, double py3, double px4, double py4) {
		boolean flag = false;
		double d = (px2 - px1) * (py4 - py3) - (py2 - py1) * (px4 - px3);
		if (d != 0) {
			double r = ((py1 - py3) * (px4 - px3) - (px1 - px3) * (py4 - py3))
					/ d;
			double s = ((py1 - py3) * (px2 - px1) - (px1 - px3) * (py2 - py1))
					/ d;
			if ((r >= 0) && (r <= 1) && (s >= 0) && (s <= 1)) {
				flag = true;
			}
		}
		return flag;
	}
	
	// 功能：判断点是否在多边形内
	// 方法：求解通过该点的水平线与多边形各边的交点
	// 结论：单边交点为奇数，成立!
	// 参数：
	// POINT p 指定的某个点
	// LPPOINT ptPolygon 多边形的各个顶点坐标（首末点可以不一致）
	public static boolean PtInPolygon(CoordPair point, List<CoordPair> APoints) {
		int nCross = 0;
		for (int i = 0; i < APoints.size(); i++) {
			CoordPair p1 = APoints.get(i);
			CoordPair p2 = APoints.get((i + 1) % APoints.size());
			// 是否落在某一个点上
			if(point.equals(p1) || point.equals(p2)){
				return true;
			}
			// 求解 y=p.y 与 p1p2 的交点
			if (p1.longitude == p2.longitude) { // p1p2 与 y=p0.y平行
				continue;
			}
			if (point.longitude < Math.min(p1.longitude, p2.longitude)) // 交点在p1p2延长线上
				continue;
			if (point.longitude >= Math.max(p1.longitude, p2.longitude)) // 交点在p1p2延长线上
				continue;
			// 求交点的 X 坐标
			// --------------------------------------------------------------
			double x = (double) (point.longitude - p1.longitude) * (double) (p2.latitude - p1.latitude) / (double) (p2.longitude - p1.longitude) + p1.latitude;
			if (x > point.latitude)
				nCross++; // 只统计单边交点
		}
		// 单边交点为偶数，点在多边形之外 ---
		return (nCross % 2 == 1);
	}
	
	public static boolean isInPolygon(CoordPair coordPair, List<CoordPair> coordPairs) {
		int nCross = 0;
		int size = coordPairs.size();
		for (int i = 0; i < size; i++) {
			CoordPair c1 = coordPairs.get(i);
			CoordPair c2 = coordPairs.get((i + 1) % size);
			// 求解 y=p.y 与 p1 p2 的交点
			// p1p2 与 y=p0.y平行
			if (c1.getY() == c2.getY())
				continue;
			// 交点在p1p2延长线上
			if (coordPair.getY() < Math.min(c1.getY(), c2.getY()))
				continue;
			// 交点在p1p2延长线上
			if (coordPair.getY() >= Math.max(c1.getY(), c2.getY()))
				continue;
			// 求交点的 X 坐标
			double x = (double) (coordPair.getY() - c1.getY()) * (double) (c2.getX() - c1.getX())
					/ (double) (c2.getY() - c1.getY()) + c1.getX();
			// 只统计单边交点
			if (x > coordPair.getX())
				nCross++;
		}
		return (nCross % 2 == 1);
	}
}













