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

    /*
            Z-
        TL-----TR           A
        | \     |           B C
    X-  |   \   |  X+
        |     \ |
        BL-----BR
            Z+
     */


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

        Vector2f midpoint2 = new Vector2f(midpoint.getX(), midpoint.getZ());

        float radius = PlayerMovement.PLAYER_RADIUS;
        etl = getExtended(midpoint2, tl, radius);
        etr = getExtended(midpoint2, tr, radius);
        ebl = getExtended(midpoint2, bl, radius);
        ebr = getExtended(midpoint2, br, radius);

        setl = getExtended(midpoint2, tl, radius - 0.1f);
        setr = getExtended(midpoint2, tr, radius - 0.1f);
        sebl = getExtended(midpoint2, bl, radius - 0.1f);
        sebr = getExtended(midpoint2, br, radius - 0.1f);

        calculateNormals();

        baseObject = new GameObject(Vector3f.zero(), build());
    }

    private Vector2f getExtended(Vector2f midpoint, Vector2f position, float extention) {
        Vector2f distance = Vector2f.subtract(position, midpoint);
        Vector2f extend = distance.normalized();
        extend.multiply(distance.magnitude() + extention);
        return Vector2f.add(midpoint, extend);
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
        Mesh mesh = new Mesh(new Vertex[] {
                // TOP
                new Vertex(new Vector3f(stl.getX(), ntl.getY(), stl.getZ()), tnor, new Vector2f(0, 1)),
                new Vertex(new Vector3f(stl.getX(), stl.getY(), stl.getZ()), tnor, new Vector2f(0, 0)),
                new Vertex(new Vector3f(str.getX(), stl.getY(), str.getZ()), tnor, new Vector2f(1, 0)),
                new Vertex(new Vector3f(str.getX(), ntl.getY(), str.getZ()), tnor, new Vector2f(1, 1)),
                // BOTTOM
                new Vertex(new Vector3f(sbr.getX(), nbr.getY(), sbr.getZ()), bnor, new Vector2f(0, 1)),
                new Vertex(new Vector3f(sbr.getX(), sbr.getY(), sbr.getZ()), bnor, new Vector2f(0, 0)),
                new Vertex(new Vector3f(sbl.getX(), sbr.getY(), sbl.getZ()), bnor, new Vector2f(1, 0)),
                new Vertex(new Vector3f(sbl.getX(), nbr.getY(), sbl.getZ()), bnor, new Vector2f(1, 1)),
                // LEFT
                new Vertex(new Vector3f(sbl.getX(), nbl.getY(), sbl.getZ()), lnor, new Vector2f(0, 1)),
                new Vertex(new Vector3f(sbl.getX(), sbl.getY(), sbl.getZ()), lnor, new Vector2f(0, 0)),
                new Vertex(new Vector3f(stl.getX(), sbl.getY(), stl.getZ()), lnor, new Vector2f(1, 0)),
                new Vertex(new Vector3f(stl.getX(), nbl.getY(), stl.getZ()), lnor, new Vector2f(1, 1)),
                // RIGHT
                new Vertex(new Vector3f(str.getX(), ntr.getY(), str.getZ()), rnor, new Vector2f(0, 1)),
                new Vertex(new Vector3f(str.getX(), str.getY(), str.getZ()), rnor, new Vector2f(0, 0)),
                new Vertex(new Vector3f(sbr.getX(), str.getY(), sbr.getZ()), rnor, new Vector2f(1, 0)),
                new Vertex(new Vector3f(sbr.getX(), ntr.getY(), sbr.getZ()), rnor, new Vector2f(1, 1)),
                // SOUTH
                new Vertex(new Vector3f(stl.getX(), stl.getY(), stl.getZ()), new Vector3f(0, -1, 0), new Vector2f(0, 0)),
                new Vertex(new Vector3f(sbl.getX(), sbl.getY(), sbl.getZ()), new Vector3f(0, -1, 0), new Vector2f(0, 1)),
                new Vertex(new Vector3f(sbr.getX(), sbr.getY(), sbr.getZ()), new Vector3f(0, -1, 0), new Vector2f(1, 1)),
                new Vertex(new Vector3f(str.getX(), str.getY(), str.getZ()), new Vector3f(0, -1, 0), new Vector2f(1, 0)),
                // NORTH
                new Vertex(new Vector3f(ntl.getX(), ntl.getY(), ntl.getZ()), new Vector3f(0, 1, 0), new Vector2f(0, 0)),
                new Vertex(new Vector3f(nbl.getX(), nbl.getY(), nbl.getZ()), new Vector3f(0, 1, 0), new Vector2f(0, 1)),
                new Vertex(new Vector3f(nbr.getX(), nbr.getY(), nbr.getZ()), new Vector3f(0, 1, 0), new Vector2f(1, 1)),
                new Vertex(new Vector3f(ntr.getX(), ntr.getY(), ntr.getZ()), new Vector3f(0, 1, 0), new Vector2f(1, 0))
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
        float radius = PlayerMovement.PLAYER_RADIUS;

        Vector3f newPosition = new Vector3f(position);
        Vector3f newVelocity = new Vector3f(velocity);

        System.out.println(etl.getX() + ":" + isWithinBounds(new Vector2f(newPosition.getX(), newPosition.getZ())));

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
                        return new Collision(new Vector3f(newPosition.getX(), stl.getY() - height - radius, newPosition.getZ()), velocity, false);
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
