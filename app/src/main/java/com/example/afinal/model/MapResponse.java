package com.example.afinal.model;

import com.google.gson.annotations.SerializedName;

public class MapResponse {
    @SerializedName("options")
    private Options options;

    public Options getOptions() {
        return options;
    }

    public static class Options {
        @SerializedName("default")
        private DefaultOptions defaultOptions;

        public DefaultOptions getDefaultOptions() {
            return defaultOptions;
        }
    }

    public static class DefaultOptions {
        private double[] center;
        private double[] bounds;
        private float zoom;
        private float minZoom;
        private float maxZoom;
        private boolean boxZoom;

        public double[] getCenter() {
            return center;
        }
        public double[] getBounds() {
            return bounds;
        }
        public float getZoom() {
            return zoom;
        }

        public float getMinZoom() {
            return minZoom;
        }

        public float getMaxZoom() {
            return maxZoom;
        }
        public boolean getBoxZoom() {
            return boxZoom;
        }
    }
}
