package app.yoojin.org.vet_project2;

import java.util.Comparator;


public class SortWithSpinner {

    /**
     * 이름 오름차순
     *
     * @author falbb
     */
    static class NameAscCompare implements Comparator<VetVO> {
        /**
         * 오름차순(ASC)
         */
        @Override
        public int compare(VetVO arg0, VetVO arg1) {
            return arg0.getHpt_name().compareTo(arg1.getHpt_name());
        }
    }

    /**
     * 이름 내림차순
     *
     * @author falbb
     */
    static class NameDescCompare implements Comparator<VetVO> {
        /**
         * 내림차순(DESC)
         */
        @Override
        public int compare(VetVO arg0, VetVO arg1) {
            return arg1.getHpt_name().compareTo(arg0.getHpt_name());
        }
    }

    /**
     * 지역 오름차순
     *
     * @author falbb
     */
    static class RegionAscCompare implements Comparator<VetVO> {
        /**
         * 오름차순(ASC)
         */
        @Override
        public int compare(VetVO arg0, VetVO arg1) {
            return arg0.getAddress().compareTo(arg1.getAddress());
        }
    }

    /**
     * 지역 내림차순
     *
     * @author falbb
     */
    static class RegionDescCompare implements Comparator<VetVO> {
        /**
         * 내림차순(DESC)
         */
        @Override
        public int compare(VetVO arg0, VetVO arg1) {
            return arg1.getAddress().compareTo(arg0.getAddress());
        }
    }

    /**
     * 평점 높은 순
     *
     * @author falbb
     */
    static class RateCompare implements Comparator<VetVO> {
        @Override
        public int compare(VetVO arg0, VetVO arg1) {
            //return Float.parseFloat(arg0.getRateAvg()) > Float.parseFloat(arg1.getRateAvg()) ? -1 : Float.parseFloat(arg0.getRateAvg()) < Float.parseFloat(arg1.getRateAvg()) ? 1 : 0;
            return Float.compare(Float.parseFloat(arg1.getRateAvg()), Float.parseFloat(arg0.getRateAvg()));
        }
    }

    /**
     * 후기 많은 순
     *
     * @author falbb
     */
    static class ReviewCompare implements Comparator<VetVO> {
        @Override
        public int compare(VetVO arg0, VetVO arg1) {
            return Integer.parseInt(arg0.getReviewCnt()) > Integer.parseInt(arg1.getReviewCnt()) ? -1 : Integer.parseInt(arg0.getReviewCnt()) < Integer.parseInt(arg1.getReviewCnt()) ? 1 : 0;
            //return Integer.compare(Integer.parseInt(arg0.getReviewCnt()), Integer.parseInt(arg1.getReviewCnt()));
        }
    }

    /**
     * 조회 많은 순
     *
     * @author falbb
     */
    static class ViewCompare implements Comparator<VetVO> {
        @Override
        public int compare(VetVO arg0, VetVO arg1) {
            return arg0.getHpt_hit() > arg1.getHpt_hit() ? -1 : arg0.getHpt_hit() < arg1.getHpt_hit() ? 1 : 0;
            //return Integer.compare(Integer.parseInt(arg0.getReviewCnt()), Integer.parseInt(arg1.getReviewCnt()));
        }
    }


}