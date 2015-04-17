package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.shadow.BasicShadowRenderer;
import com.jme3.texture.Texture.WrapMode;

public class Main extends SimpleApplication implements ActionListener {

    private BulletAppState bulletAppState;
    private Car car;

    public static void main(String[] args) throws InterruptedException {
        Menu menu = new Menu();
        menu.setVisible(true);
        boolean runned = false;
        while (!runned) {
            Thread.sleep(1000);
            if (menu.start) {
                menu.setVisible(false);
                runned = true;
                Main app = new Main();
                app.start();
            }
        }
    }

    private void setupKeys() {
        inputManager.addMapping("Lefts", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Rights", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Ups", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Downs", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Space", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Reset", new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addListener(this, "Lefts");
        inputManager.addListener(this, "Rights");
        inputManager.addListener(this, "Ups");
        inputManager.addListener(this, "Downs");
        inputManager.addListener(this, "Space");
        inputManager.addListener(this, "Reset");
    }

    private void initWorld() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        if (settings.getRenderer().startsWith("LWJGL")) {
            BasicShadowRenderer bsr = new BasicShadowRenderer(assetManager, 512);
            bsr.setDirection(new Vector3f(-0.5f, -0.3f, -0.3f).normalizeLocal());
            viewPort.addProcessor(bsr);
        }

        cam.setFrustumFar(150f);
        flyCam.setMoveSpeed(10);

        //PhysicsTestHelper.createPhysicsTestWorld(rootNode, assetManager, bulletAppState.getPhysicsSpace());
        setupFloor();

        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.5f, -1f, -0.3f).normalizeLocal());
        rootNode.addLight(dl);

        dl = new DirectionalLight();
        dl.setDirection(new Vector3f(0.5f, -0.1f, 0.3f).normalizeLocal());
        rootNode.addLight(dl);
    }

    private void initCar() {
        car = new Car(assetManager, bulletAppState);
        rootNode.attachChild(car);
    }

    public void setupFloor() {
        
        //Material mat = assetManager.loadMaterial(INPUT_MAPPING_EXIT)
        Material mat = new Material (assetManager ,"Common/MatDefs/Light/Lighting.j3md");
        mat.setFloat("Shininess", 2.0f);
        mat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/BrickWall.jpg"));
        mat.setTexture("NormalMap", assetManager.loadTexture("Textures/BrickWall_normal.jpg"));
        mat.setTexture("ParallaxMap", assetManager.loadTexture("Textures/BrickWall_height.jpg"));
        
        mat.getTextureParam("DiffuseMap").getTextureValue().setWrap(WrapMode.Repeat);
        mat.getTextureParam("NormalMap").getTextureValue().setWrap(WrapMode.Repeat);
        mat.getTextureParam("ParallaxMap").getTextureValue().setWrap(WrapMode.Repeat);

        Box floor = new Box(Vector3f.ZERO, 140, 1f, 140);
        floor.scaleTextureCoordinates(new Vector2f(112.0f, 112.0f));
        Geometry floorGeom = new Geometry("Floor", floor);
        floorGeom.setShadowMode(ShadowMode.Receive);
        floorGeom.setMaterial(mat);

        floorGeom.addControl(new RigidBodyControl(0f));
        floorGeom.setLocalTranslation(new Vector3f(0f, -6, 0f));
//        floorGeom.attachDebugShape(assetManager);
        rootNode.attachChild(floorGeom);
        getPhysicsSpace().add(floorGeom);
    }

    private PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }

    @Override
    public void simpleInitApp() {
        setupKeys();
        initWorld();
        setupFloor();
        initCar();

    }

    @Override
    public void simpleUpdate(float tpf) {
        cam.lookAt(car.getWorldTranslation(), Vector3f.UNIT_Y);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("Lefts")) {
            if (value) {
                car.steeringValue += .5f;
            } else {
                car.steeringValue += -.5f;
            }
            car.steer();
        } else if (binding.equals("Rights")) {
            if (value) {
                car.steeringValue += -.5f;
            } else {
                car.steeringValue += .5f;
            }
            car.steer();
        } //note that our fancy car actually goes backwards..
        else if (binding.equals("Ups")) {
            if (value) {
                car.accelerationValue -= 800;
            } else {
                car.accelerationValue += 800;
            }
            car.accelerate();
            car.setCollisionShape();
        } else if (binding.equals("Downs")) {
            if (value) {
                car.brake(40f);
            } else {
                car.brake(0f);
            }
        } else if (binding.equals("Reset") && value) {
            car.reset();
        }
    }
}
