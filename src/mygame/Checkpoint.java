/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

/**
 *
 * @author snyx
 */
public class Checkpoint extends Node implements PhysicsCollisionListener {
    
    public int scoreVal;
    private Box box;
    public Geometry boxGeometry;
    private PhysicsSpace space;
    
    public Checkpoint(PhysicsSpace space, Material material, float offset, int scoreVal) {
        super();
        box = new Box(0.25f, 0.25f, 0.25f);
        boxGeometry = new Geometry("Box", box);
        boxGeometry.setMaterial(material);
        boxGeometry.setLocalTranslation(offset, 5, -3);
        //RigidBodyControl automatically uses box collision shapes when attached to single geometry with box mesh
        this.boxGeometry.addControl(new RigidBodyControl(2));
        this.space = space;
        this.space.add(this.boxGeometry);
        this.attachChild(boxGeometry);
        this.scoreVal = scoreVal;
    }
    
    public void collision(PhysicsCollisionEvent event) {
        Main.score += scoreVal;
        this.removeFromParent();
        space.remove(this.boxGeometry);
        parent.updateGeometricState();
    }
}
