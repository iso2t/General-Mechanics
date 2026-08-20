package general.mechanics.client.model;

import com.mojang.blaze3d.platform.Transparency;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/**
 * Quad baker matching the 1.20 cable renderer: positions and UVs are supplied
 * per vertex, and a rotation changes their order rather than the face itself.
 */
public final class BakedModelHelper {

	private BakedModelHelper () {
	}

	public static BakedQuad quad (Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4, Material.Baked material, int rotation) {
		Vec3[] vertices = { v1, v2, v3, v4 };
		int offset = Math.floorMod(rotation, 4);
		Vec3 p1 = vertices[offset];
		Vec3 p2 = vertices[(offset + 1) % 4];
		Vec3 p3 = vertices[(offset + 2) % 4];
		Vec3 p4 = vertices[(offset + 3) % 4];
		Vec3 normal = p3.subtract(p2).cross(p1.subtract(p2)).normalize();

		var sprite = material.sprite();
		var transparency = material.forceTranslucent() ? Transparency.TRANSLUCENT : sprite.transparency();
		var info = BakedQuad.MaterialInfo.of(material, transparency, -1, true, 0);
		return new BakedQuad(vector(p1), vector(p2), vector(p3), vector(p4), UVPair.pack(sprite.getU(0), sprite.getV(0)), UVPair.pack(sprite.getU(0), sprite.getV(1)), UVPair.pack(sprite.getU(1), sprite.getV(1)), UVPair.pack(sprite.getU(1), sprite.getV(0)), directionFor(normal), info);
	}

	public static Vec3 v (double x, double y, double z) {
		return new Vec3(x, y, z);
	}

	private static Vector3f vector (Vec3 vertex) {
		return new Vector3f((float) vertex.x, (float) vertex.y, (float) vertex.z);
	}

	private static Direction directionFor (Vec3 normal) {
		double x = Math.abs(normal.x);
		double y = Math.abs(normal.y);
		double z = Math.abs(normal.z);
		if (x >= y && x >= z) return normal.x >= 0 ? Direction.EAST : Direction.WEST;
		if (y >= z) return normal.y >= 0 ? Direction.UP : Direction.DOWN;
		return normal.z >= 0 ? Direction.SOUTH : Direction.NORTH;
	}
}
