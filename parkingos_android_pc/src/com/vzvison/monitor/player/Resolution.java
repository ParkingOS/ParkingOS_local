package com.vzvison.monitor.player;

import java.util.Arrays;


public class Resolution {
	private static final int[] RESOLUTION_D1 	= {704,576};
	private static final int[] RESOLUTION_HD1 	= {704,288};
	private static final int[] RESOLUTION_CIF 	= {352,288};
	private static final int[] RESOLUTION_QCIF 	= {176,144};
	private static final int[] RESOLUTION_QQVGA = {160,112};
	private static final int[] RESOLUTION_QVGA 	= {320,240};
	private static final int[] RESOLUTION_VGA 	= {640,480};
	private static final int[] RESOLUTION_SVGA 	= {800,600};
	private static final int[] RESOLUTION_XGA 	= {1024,768};
	private static final int[] RESOLUTION_XVGA 	= {1280,960};
	private static final int[] RESOLUTION_WXGA 	= {1280,800};
	private static final int[] RESOLUTION_UXGA 	= {1600,120};
	private static final int[] RESOLUTION_D2 	= {720,480};
	private static final int[] RESOLUTION_D4 	= {1280,720};
	private static final int[] RESOLUTION_D5 	= {1920,1080};
	
	public static final int D1 		= 0;
	public static final int HD1 	= 1;
	public static final int CIF 	= 2;
	public static final int QCIF 	= 3;
	public static final int QQVGA 	= 4;
	public static final int QVGA 	= 5;
	public static final int VGA 	= 6;
	public static final int SVGA 	= 7;
	public static final int XGA 	= 8;
	public static final int XVGA 	= 9;
	public static final int WXGA 	= 10;
	public static final int UXGA 	= 11;
	public static final int D2 		= 12;
	public static final int D4 		= 13;
	public static final int D5 		= 14;
	
	public static final int[] getResolution(int mask) {
		int[] resolution = {0,0};
		switch (mask) {
		case D1:
			resolution = RESOLUTION_D1;
			break;
		case HD1:
			resolution = RESOLUTION_HD1;
			break;
		case CIF:
			resolution = RESOLUTION_CIF;
			break;
		case QCIF:
			resolution = RESOLUTION_QCIF;
			break;
		case QQVGA:
			resolution = RESOLUTION_QQVGA;
			break;
		case QVGA:
			resolution = RESOLUTION_QVGA;
			break;
		case VGA:
			resolution = RESOLUTION_VGA;
			break;
		case SVGA:
			resolution = RESOLUTION_SVGA;
			break;
		case XGA:
			resolution = RESOLUTION_XGA;
			break;
		case XVGA:
			resolution = RESOLUTION_XVGA;
			break;
		case WXGA:
			resolution = RESOLUTION_WXGA;
			break;
		case UXGA:
			resolution = RESOLUTION_UXGA;
			break;
		case D2:
			resolution = RESOLUTION_D2;
			break;
		case D4:
			resolution = RESOLUTION_D4;
			break;
		case D5:
			resolution = RESOLUTION_D5;
			break;
		default:
			break;
		}
		return resolution;
	}
	
	public static final int[] getKaerhResolution(int mask) {
		if(0 == mask) { //D1
			return getResolution(D1);
		} else if(1 == mask) { //QCIF
			return getResolution(QCIF);
		} else if(2 == mask) { //CIF
			return getResolution(CIF);
		} else if(3 == mask) { //HD1
			return getResolution(HD1);
		}
		return new int[]{0,0};
	}
	
	public static final String getgetResolutionType(int width, int height) {
		String result = "";
		int[] resolution = {width,height};
		if(Arrays.equals(resolution, RESOLUTION_D1)) {
			result = "D1";
		} else if(Arrays.equals(resolution, RESOLUTION_HD1)) {
			result = "HD1";
		} else if(Arrays.equals(resolution, RESOLUTION_CIF)) {
			result = "CIF";
		} else if(Arrays.equals(resolution, RESOLUTION_QCIF)) {
			result = "QCIF";
		}else if(Arrays.equals(resolution, RESOLUTION_QQVGA)) {
			result = "QQVGA";
		}else if(Arrays.equals(resolution, RESOLUTION_QVGA)) {
			result = "QVGA";
		}else if(Arrays.equals(resolution, RESOLUTION_VGA)) {
			result = "VGA";
		}else if(Arrays.equals(resolution, RESOLUTION_SVGA)) {
			result = "SVGA";
		}else if(Arrays.equals(resolution, RESOLUTION_XGA)) {
			result = "XGA";
		}else if(Arrays.equals(resolution, RESOLUTION_XVGA)) {
			result = "XVGA";
		}else if(Arrays.equals(resolution, RESOLUTION_WXGA)) {
			result = "WXGA";
		}else if(Arrays.equals(resolution, RESOLUTION_UXGA)) {
			result = "UXGA";
		}else if(Arrays.equals(resolution, RESOLUTION_D2)) {
			result = "D2";
		}else if(Arrays.equals(resolution, RESOLUTION_D4)) {
			result = "D4";
		}else if(Arrays.equals(resolution, RESOLUTION_D5)) {
			result = "D5";
		} 
		
		return result;
	}

}
