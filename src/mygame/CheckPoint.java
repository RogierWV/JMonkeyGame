package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;

public class CheckPoint  {
    
    private AssetManager assetManager;
    private Geometry geometry;
    private Material material;
    private boolean passed;
    
    public CheckPoint() {
        passed = false;
    }
    
    public void onCollision(Object other) {
        if(other instanceof Car) {
            passed = true;
            ((Car)other).score++;
            boolean r = true;
            for (CheckPoint c : Main.checkpoints) {
                if(!c.isPassed()) r = false;
            }
            if (r) {
                ((Car)other).score *= 1.1;
                for (CheckPoint c : Main.checkpoints) {
                    c.setPassed(false);
                }
            }
        }
    }
    
    public void setPassed(boolean p) {
        passed = p;
    }
    
    public boolean isPassed() {
        return passed;
    }
}
