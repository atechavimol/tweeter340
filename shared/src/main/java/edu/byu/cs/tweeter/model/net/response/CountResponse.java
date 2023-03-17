package edu.byu.cs.tweeter.model.net.response;

public class CountResponse extends Response{
    private int count;

    public CountResponse(int count) {
        super(true);
        this.count = count;
    }

    public CountResponse(String message) {
        super(false, message);
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
