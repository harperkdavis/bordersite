package engine.components;

import engine.collision.Collision;
import engine.graphics.Material;
import engine.graphics.mesh.Mesh;
import engine.graphics.vertex.Vertex;
import engine.math.Mathf;
import engine.math.Vector2f;
import engine.math.Vector3f;
import engine.objects.GameObject;
import game.PlayerMovement;

public class BlockComponent implements Component {

    private Vector3f stl, str, sbl, sbr, ntl, ntr, nbl, nbr;
    private Vector3f tnor, bnor, lnor, rnor;
    private Vector2f tl, tr, bl, br, etl, etr, ebl, ebr, setl, setr, sebl, sebr;
    
    private GameObject baseObject;
    private Material material;
    
    private float height;
    private float lrScale = 1, yScale = 1, udScale = 1;
    private float tiling = 2.0f;
    private boolean mesh = true, collision = true;

    /*
            Z-
        TL-----TR           A
        | \     |           B C
    X-  |   \   |  X+
        |     \ |
        BL-----BR
            Z+
     */

    public BlockComponent(Vector3f a, Vector3f b, Vector3f c, float height, Material material, float tiling) {
        this(a, b, c, height, material);
        this.tiling = tiling;
    }

    public BlockComponent(Vector3f a, Vector3f b, Vector3f c, float height, Material material, float tiling, boolean mesh, boolean collision) {
        this(a, b, c, height, material, tiling);
        this.mesh = mesh;
        this.collision = collision;
    }


    public BlockComponent(Vector3f a, Vector3f b, Vector3f c, float height, Material material) {
        this.stl = a;
        this.sbr = c;
        this.sbl = b;

        this.sbr.setY(stl.getY());
        this.sbl.setY(stl.getY());

        Vector3f midpoint = Vector3f.add(a, c).divide(2);
        this.str = Vector3f.add(sbl, Vector3f.subtract(midpoint, sbl).multiply(2));
        
        this.height = height;
        this.material = material;

        this.ntl = new Vector3f(stl).add(0, height, 0);
        this.ntr = new Vector3f(str).add(0, height, 0);
        this.nbl = new Vector3f(sbl).add(0, height, 0);
        this.nbr = new Vector3f(sbr).add(0, height, 0);

        tl = new Vector2f(stl.getX(), stl.getZ());
        tr = new Vector2f(str.getX(), str.getZ());
        bl = new Vector2f(sbl.getX(), sbl.getZ());
        br = new Vector2f(sbr.getX(), sbr.getZ());

        lrScale = Vector2f.subtract(tl, tr).magnitude();
        udScale = Vector2f.subtract(tl, bl).magnitude();
        yScale = ntl.getY() - stl.getY();

        float radius = PlayerMovement.PLAYER_RADIUS;

        calculateNormals();

        etl = getExtended(bl, tl, tr, lnor, tnor, radius);
        etr = getExtended(tl, tr, br, tnor, rnor, radius);
        ebr = getExtended(tr, br, bl, rnor, bnor, radius);
        ebl = getExtended(br, bl, tl, bnor, lnor, radius);

        setl = getExtended(bl, tl, tr, lnor, tnor, radius - 0.1f);
        setr = getExtended(tl, tr, br, tnor, rnor, radius - 0.1f);
        sebr = getExtended(tr, br, bl, rnor, bnor, radius - 0.1f);
        sebl = getExtended(br, bl, tl, bnor, lnor, radius - 0.1f);

        baseObject = new GameObject(Vector3f.zero(), build());
    }

    private Vector2f getExtended(Vector2f a, Vector2f b, Vector2f c, Vector3f abnor, Vector3f bcnor, float extention) {
        Vector2f eab = new Vector2f(a.getX() + abnor.getX() * extention, a.getY() + abnor.getZ() * extention);
        Vector2f eba = new Vector2f(b.getX() + abnor.getX() * extention, b.getY() + abnor.getZ() * extention);

        Vector2f ebc = new Vector2f(b.getX() + bcnor.getX() * extention, b.getY() + bcnor.getZ() * extention);
        Vector2f ecb = new Vector2f(c.getX() + bcnor.getX() * extention, c.getY() + bcnor.getZ() * extention);

        return Mathf.intersectPoint(eab.getX(), eab.getY(), eba.getX(), eba.getY(), ebc.getX(), ebc.getY(), ecb.getX(), ecb.getY());
    }

    private void calculateNormals() {
        Vector3f nsnor = getSideNormal(stl, str);
        Vector3f lrnor = getSideNormal(str, sbr);
        tnor = new Vector3f(nsnor).multiply(-1);
        bnor = new Vector3f(nsnor);
        rnor = new Vector3f(lrnor).multiply(-1);
        lnor = new Vector3f(lrnor);
    }

    private Vector3f getSideNormal(Vector3f a, Vector3f b) {
        Vector3f c = Vector3f.add(a, Vector3f.oneY());
        Vector3f sa = Vector3f.subtract(b, a), sb = Vector3f.subtract(c, a);
        return Vector3f.cross(sa, sb).normalize();
    }

    private Mesh build() {

        if (!mesh) {
            return new Mesh(new Vertex[]{}, new int[]{}, Material.DEFAULT);
        }

        Mesh mesh = new Mesh(new Vertex[] {
                // TOP
                new Vertex(new Vector3f(stl.getX(), ntl.getY(), stl.getZ()), tnor, new Vector2f(0, yScale / tiling)),
                new Vertex(new Vector3f(stl.getX(), stl.getY(), stl.getZ()), tnor, new Vector2f(0, 0 / tiling)),
                new Vertex(new Vector3f(str.getX(), stl.getY(), str.getZ()), tnor, new Vector2f(lrScale / tiling, 0 / tiling)),
                new Vertex(new Vector3f(str.getX(), ntl.getY(), str.getZ()), tnor, new Vector2f(lrScale / tiling, yScale / tiling)),
                // BOTTOM
                new Vertex(new Vector3f(sbr.getX(), nbr.getY(), sbr.getZ()), bnor, new Vector2f(0, ntl.getY() / tiling)),
                new Vertex(new Vector3f(sbr.getX(), sbr.getY(), sbr.getZ()), bnor, new Vector2f(0, stl.getY() / tiling)),
                new Vertex(new Vector3f(sbl.getX(), sbr.getY(), sbl.getZ()), bnor, new Vector2f(lrScale / tiling, stl.getY() / tiling)),
                new Vertex(new Vector3f(sbl.getX(), nbr.getY(), sbl.getZ()), bnor, new Vector2f(lrScale / tiling, ntl.getY() / tiling)),
                // LEFT
                new Vertex(new Vector3f(sbl.getX(), nbl.getY(), sbl.getZ()), lnor, new Vector2f(0, yScale / tiling)),
                new Vertex(new Vector3f(sbl.getX(), sbl.getY(), sbl.getZ()), lnor, new Vector2f(0, 0)),
                new Vertex(new Vector3f(stl.getX(), sbl.getY(), stl.getZ()), lnor, new Vector2f(udScale / tiling, 0)),
                new Vertex(new Vector3f(stl.getX(), nbl.getY(), stl.getZ()), lnor, new Vector2f(udScale / tiling, yScale / tiling)),
                // RIGHT
                new Vertex(new Vector3f(str.getX(), ntr.getY(), str.getZ()), rnor, new Vector2f(0, yScale / tiling)),
                new Vertex(new Vector3f(str.getX(), str.getY(), str.getZ()), rnor, new Vector2f(0, 0)),
                new Vertex(new Vector3f(sbr.getX(), str.getY(), sbr.getZ()), rnor, new Vector2f(udScale / tiling, 0)),
                new Vertex(new Vector3f(sbr.getX(), ntr.getY(), sbr.getZ()), rnor, new Vector2f(udScale / tiling, yScale / tiling)),
                // SOUTH
                new Vertex(new Vector3f(stl.getX(), stl.getY(), stl.getZ()), new Vector3f(0, -1, 0), new Vector2f(stl.getX() / tiling, sbl.getZ() / tiling)),
                new Vertex(new Vector3f(sbl.getX(), sbl.getY(), sbl.getZ()), new Vector3f(0, -1, 0), new Vector2f(sbl.getX() / tiling, stl.getZ() / tiling)),
                new Vertex(new Vector3f(sbr.getX(), sbr.getY(), sbr.getZ()), new Vector3f(0, -1, 0), new Vector2f(sbr.getX() / tiling, stl.getZ() / tiling)),
                new Vertex(new Vector3f(str.getX(), str.getY(), str.getZ()), new Vector3f(0, -1, 0), new Vector2f(str.getX() / tiling, sbl.getZ() / tiling)),
                // NORTH
                new Vertex(new Vector3f(ntl.getX(), ntl.getY(), ntl.getZ()), new Vector3f(0, 1, 0), new Vector2f(ntl.getX() / tiling, stl.getZ() / tiling)),
                new Vertex(new Vector3f(nbl.getX(), nbl.getY(), nbl.getZ()), new Vector3f(0, 1, 0), new Vector2f(nbl.getX() / tiling, sbl.getZ() / tiling)),
                new Vertex(new Vector3f(nbr.getX(), nbr.getY(), nbr.getZ()), new Vector3f(0, 1, 0), new Vector2f(nbr.getX() / tiling, sbl.getZ() / tiling)),
                new Vertex(new Vector3f(ntr.getX(), ntr.getY(), ntr.getZ()), new Vector3f(0, 1, 0), new Vector2f(ntr.getX() / tiling, stl.getZ() / tiling))
        }, new int[] {
                0,  2,  1,
                0,  3,  2,
                4,  6,  5,
                4,  7,  6,
                8,  10, 9,
                8,  11, 10,
                12, 14, 13,
                12, 15, 14,
                16, 18, 17,
                16, 19, 18,
                20, 22, 21,
                20, 23, 22
        }, material);
        return mesh;
    }

    @Override
    public Collision getCollision(Vector3f previous, Vector3f position, Vector3f velocity, float height, boolean isGrounded) {
        if (!collision) {
            return new Collision(position, velocity, false);
        }

        float radius = PlayerMovement.PLAYER_RADIUS;

        Vector3f newPosition = new Vector3f(position);
        Vector3f newVelocity = new Vector3f(velocity);

        if ((position.getY() - radius <= ntl.getY() && position.getY() + height + radius >= stl.getY())) {
            newPosition = sideCollision(newPosition, tl, tr, etr, etl, tnor);
            newPosition = sideCollision(newPosition, bl, br, ebr, ebl, bnor);
            newPosition = sideCollision(newPosition, tl, bl, ebl, etl, lnor);
            newPosition = sideCollision(newPosition, tr, br, ebr, etr, rnor);
        }

        if (newPosition.equals(position) && !isGrounded) {
            if (isWithinBounds(new Vector2f(newPosition.getX(), newPosition.getZ()))) {
                if (newPosition.getY() > (ntl.getY() + stl.getY()) / 2) {
                    if (newPosition.getY() < ntl.getY() + radius && velocity.getY() < 0) {
                        return new Collision(new Vector3f(newPosition.getX(), ntl.getY() + radius, newPosition.getZ()), velocity, true);
                    }
                } else {
                    if (newPosition.getY() + height + radius > stl.getY() && velocity.getY() >= 0) {
                        return new Collision(new Vector3f(newPosition.getX(), stl.getY() - height - radius, newPosition.getZ()), new Vector3f(velocity.getX(), 0, velocity.getZ()), false);
                    }
                }
            }
        }

        return new Collision(newPosition, velocity, isGrounded(newPosition));
    }

    private boolean isWithinBounds(Vector2f position) {
        return Mathf.pointTriangle(position, setl, sebl, sebr) || Mathf.pointTriangle(position, setl, sebr, setr);
    }

    @Override
    public boolean isGrounded(Vector3f pos) {
        if (isWithinBounds(new Vector2f(pos.getX(), pos.getZ()))) {
            if (pos.getY() > (ntl.getY() + stl.getY()) / 2) {
                if (pos.getY() - 0.1f < ntl.getY() + PlayerMovement.PLAYER_RADIUS) {
                    return true;
                }
            }
        }
        return false;
    }

    private Vector3f sideCollision(Vector3f pos, Vector2f a, Vector2f b, Vector2f c, Vector2f d, Vector3f normal) {
        if (Mathf.pointTrapezoid(new Vector2f(pos.getX(), pos.getZ()), a, b, c, d)) {
            float distance = Mathf.pointLine(pos.getX(), pos.getZ(), c.getX(), c.getY(), d.getX(), d.getY()) * 1.05f;
            return new Vector3f(pos.getX() + normal.getX() * distance, pos.getY(), pos.getZ() + normal.getZ() * distance);
        }
        return pos;
    }

    @Override
    public GameObject getObject() {
        return baseObject;
    }



}
