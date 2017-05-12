package com.gentledevs.chatmodule;

/**
 * Created by Yura Stetsyc on 4/18/17.
 */

public class ChatAdapterOptions {

    private int mLoadingGap = 10;

    private int mColorTextDate = 0x2b3857;
    private int mColorSelf = 0x0EB0A0;
    private int mColorOther = 0x97a1aa;

    public ChatAdapterOptions(int mLoadingGap, int colorTextDate, int colorSelf, int colorOther) {
        this.mLoadingGap = mLoadingGap;
        mColorTextDate = colorTextDate;
        mColorSelf = colorSelf;
        mColorOther = colorOther;
    }

    public int getLoadingGap() {
        return mLoadingGap;
    }

    public void setLoadingGap(int loadingGap) {
        mLoadingGap = loadingGap;
    }

    public int getColorTextDate() {
        return mColorTextDate;
    }

    public void setColorTextDate(int colorTextDate) {
        mColorTextDate = colorTextDate;
    }

    public int getColorSelf() {
        return mColorSelf;
    }

    public void setColorSelf(int colorSelf) {
        mColorSelf = colorSelf;
    }

    public int getColorOther() {
        return mColorOther;
    }

    public void setColorOther(int colorOther) {
        mColorOther = colorOther;
    }

    public static class Builder {
        private int mLoadingGap = 10;

        private int mColorTextDate = 0x2b3857;
        private int mColorSelf = 0x0EB0A0;
        private int mColorOther = 0x97a1aa;

        public Builder setLoadingGap(int loadingGap) {
            this.mLoadingGap = loadingGap;
            return this;
        }

        public Builder setColorTextDate(int colorTextDate) {
            mColorTextDate = colorTextDate;
            return this;
        }

        public Builder setColorSelf(int colorSelf) {
            mColorSelf = colorSelf;
            return this;
        }

        public Builder setColorOther(int colorOther) {
            mColorOther = colorOther;
            return this;
        }

        public ChatAdapterOptions build() {
            return new ChatAdapterOptions(mLoadingGap, mColorTextDate, mColorSelf, mColorOther);
        }
    }

}
