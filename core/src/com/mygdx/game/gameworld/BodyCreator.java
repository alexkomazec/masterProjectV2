package com.mygdx.game.gameworld;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.config.GameConfig;

public class BodyCreator {
	public static final int STEEL = 0;
	public static final int WOOD = 1;
	public static final int RUBBER = 2;
	public static final int STONE = 3;

	private static BodyCreator thisInstance;
	private final float DEGTORAD = 0.0174533f;
		
	private BodyCreator(){
	}
	
	public static BodyCreator getInstance(){
		if(thisInstance == null)
		{
			thisInstance = new BodyCreator();
		}
		return thisInstance;
	}

	public Body makeBoxPolyBody(Rectangle rectangle,int material,
								BodyType bodyType, World world, boolean fixedRotation)
	{

		// create a definition
		BodyDef boxBodyDef = new BodyDef();
		boxBodyDef.type = bodyType;
		boxBodyDef.position.x = (rectangle.getX() + rectangle.getWidth()/2) * GameConfig.DIVIDE_BY_PPM;
		boxBodyDef.position.y = (rectangle.getY() + rectangle.getHeight()/2) * GameConfig.DIVIDE_BY_PPM;
		boxBodyDef.fixedRotation = fixedRotation;
		
		//create the body to attach said definition
		Body boxBody = world.createBody(boxBodyDef);
		PolygonShape poly = new PolygonShape();
		poly.setAsBox(
				(rectangle.getWidth()/2)  * GameConfig.DIVIDE_BY_PPM,
				(rectangle.getHeight()/2) * GameConfig.DIVIDE_BY_PPM);
		boxBody.createFixture(makeFixture(material,poly));
		poly.dispose();

		return boxBody;
	}

	public Body makeBullet(Rectangle rectangle, float radius, int material, World world, BodyType bodyType)
	{
		Body body = makeCirclePolyBody( rectangle,  material,  bodyType,  world, false);
			for(Fixture fix :body.getFixtureList()){
			fix.setSensor(true);
			}
		body.setBullet(true);
		return body;
	}
	
	public Body makeCirclePolyBody(Rectangle rectangle,
								   int material, BodyType bodyType,
								   World world, boolean fixedRotation)
	{
		// create a definition
		BodyDef boxBodyDef = new BodyDef();
		boxBodyDef.type = bodyType;
		boxBodyDef.position.x = (rectangle.getX() + rectangle.getWidth()/2) * GameConfig.DIVIDE_BY_PPM;
		boxBodyDef.position.y = (rectangle.getY() + rectangle.getHeight()/2) * GameConfig.DIVIDE_BY_PPM;
		boxBodyDef.fixedRotation = fixedRotation;
		
		//create the body to attach said definition
		Body boxBody = world.createBody(boxBodyDef);
		CircleShape circleShape = new CircleShape();
		float radius = rectangle.getWidth()/2;
		circleShape.setRadius(radius * GameConfig.DIVIDE_BY_PPM);
		boxBody.createFixture(makeFixture(material,circleShape));
		circleShape.dispose();
		return boxBody;
	}
	
	public Body makeSensorBody(float posx, float posy, float radius , World world, BodyType bodyType){
		return makeSensorBody(posx,posy,radius,bodyType,world, false);
	}
	
	public Body makeSensorBody(float posx, float posy, float radius , BodyType bodyType, World world, boolean fixedRotation){
		// create a definition
		BodyDef boxBodyDef = new BodyDef();
		boxBodyDef.type = bodyType;
		boxBodyDef.position.x = posx;
		boxBodyDef.position.y = posy;
		boxBodyDef.fixedRotation = fixedRotation;
		
		//create the body to attach said definition
		Body boxBody = world.createBody(boxBodyDef);
		this.makeSensorFixture(boxBody, radius);
		return boxBody;
	}
	
	static public FixtureDef makeFixture(int material, Shape shape){
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		
		switch(material){
		case STEEL:
			fixtureDef.density = 1f;
			fixtureDef.friction = 0.3f;
			fixtureDef.restitution = 0.1f;
			break;
		case WOOD:
			fixtureDef.density = 0.5f;
			fixtureDef.friction = 0.7f;
			fixtureDef.restitution = 0.3f;
			break;
		case RUBBER:
			fixtureDef.density = 1f;
			fixtureDef.friction = 0f;
			fixtureDef.restitution = 1f;
			break;
		case STONE:
			fixtureDef.density = 1f;
			fixtureDef.friction = 0.5f;
			fixtureDef.restitution = 0f;
			break;
		default:
				fixtureDef.density = 7f;
				fixtureDef.friction = 0.5f;
				fixtureDef.restitution = 0.3f;
		}

		return fixtureDef;
	}
	
	public void makeSensorFixture(Body body, float size){
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.isSensor = true;
		CircleShape circleShape = new CircleShape();
		circleShape.setRadius(size * GameConfig.DIVIDE_BY_PPM);
		fixtureDef.shape = circleShape;
		body.createFixture(fixtureDef);
		circleShape.dispose();
		
	}
	
	public void makeConeSensor(Body body, float size){
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.isSensor = true;
		
		
		PolygonShape polygon = new PolygonShape();
		
		float radius = size;
		Vector2[] vertices = new Vector2[5];
		vertices[0] = new Vector2(0,0);
		for (int i = 2; i < 6; i++) {
		    float angle = (float) (i  / 6.0 * 145 * DEGTORAD); // convert degrees to radians
		    vertices[i-1] = new Vector2( radius * ((float)Math.cos(angle)), radius * ((float)Math.sin(angle)));
		}
		polygon.set(vertices);
		//polygon.setRadius(size);
		fixtureDef.shape = polygon;
		body.createFixture(fixtureDef);
		polygon.dispose();
	}
	
	/*
	 * Make a body from a set of vertices
	 */
	public Body makePolygonShapeBody(Vector2[] vertices, float posx, float posy, int material, World world, BodyType bodyType){
		BodyDef boxBodyDef = new BodyDef();
		boxBodyDef.type = bodyType;
		boxBodyDef.position.x = posx;
		boxBodyDef.position.y = posy;
		Body boxBody = world.createBody(boxBodyDef);
		
		PolygonShape polygon = new PolygonShape();
		polygon.set(vertices);
		boxBody.createFixture(makeFixture(material,polygon));
		polygon.dispose();
		
		return boxBody;
	}
	
	public void makeAllFixturesSensors(Body bod){
		for(Fixture fix :bod.getFixtureList()){
			fix.setSensor(true);
		}
	}
	
	public void setAllFixtureMask(Body bod, Short filter){
		Filter fil = new Filter();
		fil.groupIndex = filter;
		for(Fixture fix :bod.getFixtureList()){
			fix.setFilterData(fil);
		}
	}
	
	public Body addCircleFixture(Body bod, float x, float y, float size, int material, boolean sensor){
		CircleShape circleShape = new CircleShape();
		circleShape.setRadius(size /2);
		circleShape.setPosition(new Vector2(x,y));
		FixtureDef fix = makeFixture(material,circleShape);
		fix.isSensor = sensor;
		bod.createFixture(fix);
		circleShape.dispose();
		return bod;
	}
	
}