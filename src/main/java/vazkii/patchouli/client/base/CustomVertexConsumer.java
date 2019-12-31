package vazkii.patchouli.client.base;

public interface CustomVertexConsumer {
    void patchouli_drawWithCamera(float x, float y, float z);
    void patchouli_drawWithCustomState(Runnable custom);
}
