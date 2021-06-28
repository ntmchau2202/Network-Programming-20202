package server.entity.network;

public interface IProcessor {
    boolean isStop = false;
    boolean isCancel = false;

    public void cancelProcessingRequest();
    public void stopAll();
}
