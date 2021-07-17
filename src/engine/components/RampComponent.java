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

public class RampComponent implements Component {

    private final Vector3f stl, str, sbl, sbr, ntl, ntr, nbl, nbr;
    private Vector3f tnor, bnor, lnor, rnor, nnor, frontnormal, leftnormal, rightnormal;
    private Vector2f tl, tr, bl, br,
            etl, etr, ebl, ebr,
            setl, setr, sebl, sebr,
            etopl, etopr, ebtml, ebtmr,
            topl, topr, btml, btmr, intopl, intopr,
            stopl, stopr, sbotl, sbotr;

    private GameObject baseObject;
    private Material material;

    private int direction;
    private float crossMagnitude;
    private float height, slope;
    private float tiling = 2.0f;
    private boolean mesh = true, collision = true;

    /*
            Z-
        TL-----TR
        | \     |           0
    X-  |   \   |  X+     3   1
        |     \ |           2
        BL-----BR
            Z+
     */

    public RampComponent(Vector3f a, Vector3f b, Vector3f c, float height, int direction, Material material, float tiling, boolean mesh, boolean collision) {
        this(a, b, c, height, direction, material, tiling);
        this.collision = collision;
        this.mesh = mesh;
    }

    public RampComponent(Vector3f a, Vector3f b, Vector3f c, float height, int direction, Material material, float tiling) {
        this(a, b, c, height, direction, material);
        this.tiling = tiling;
    }

    public RampComponent(Vector3f a, Vector3f b, Vector3f c, float height, int direction, Material material) {
        this.stl = a;
        this.sbr = c;
        this.sbl = b;

        this.sbr.setY(stl.getY());
        this.sbl.setY(stl.getY());

        Vector3f midpoint = Vector3f.add(a, c).divide(2);
        this.str = Vector3f.add(sbl, Vector3f.subtract(midpoint, sbl).multiply(2));

        for (int i = 0; i < direction; i++) {
            // TODO add rotation
        }
        
        this.height = height;
        this.material = material;

        this.ntl = stl.plus(0, height, 0);
        this.ntr = str.plus(0, height, 0);
        this.nbr = sbr.plus(0, 0, 0);
        this.nbl = sbl.plus(0, 0, 0);

        tl = new Vector2f(stl.getX(), stl.getZ());
        tr = new Vector2f(str.getX(), str.getZ());
        bl = new Vector2f(sbl.getX(), sbl.getZ());
        br = new Vector2f(sbr.getX(), sbr.getZ());

        float radius = PlayerMovement.PLAYER_RADIUS;
        calculateSideNormals();

        etl = getExtended(bl, tl, tr, lnor, tnor, radius);
        etr = getExtended(tl, tr, br, tnor, rnor, radius);
        ebr = getExtended(tr, br, bl, rnor, bnor, radius);
        ebl = getExtended(br, bl, tl, bnor, lnor, radius);

        etopl = new Vector2f(etl);
        etopr = new Vector2f(etr);
        ebtmr = new Vector2f(ebr);
        ebtml = new Vector2f(ebl);
        topl = new Vector2f(tl);
        topr = new Vector2f(tr);
        btmr = new Vector2f(br);
        btml = new Vector2f(bl);

        calculateTopNormal();

        slope = Vector3f.dot(Vector3f.oneY(), nnor.normalized());

        leftnormal = lnor;
        frontnormal = tnor;
        rightnormal = rnor;

        intopl = new Vector2f(etopl.getX() - frontnormal.getX() * radius * 2, etopl.getY() - frontnormal.getZ() * radius * 2);
        intopr = new Vector2f(etopr.getX() - frontnormal.getX() * radius * 2, etopr.getY() - frontnormal.getZ() * radius * 2);

        setl = getExtended(bl, tl, tr, lnor, tnor, radius - 0.1f);
        setr = getExtended(tl, tr, br, tnor, rnor, radius - 0.1f);
        sebr = getExtended(tr, br, bl, rnor, bnor, radius - 0.1f);
        sebl = getExtended(br, bl, tl, bnor, lnor, radius - 0.1f);

        stopl = Mathf.intersectPoint(setl.getX(), setl.getY(), sebl.getX(), sebl.getY(), topl.getX(), topl.getY(), topr.getX(), topr.getY());
        stopr = Mathf.intersectPoint(setr.getX(), setr.getY(), sebr.getX(), sebr.getY(), topl.getX(), topl.getY(), topr.getX(), topr.getY());

        sbotl = Mathf.intersectPoint(setl.getX(), setl.getY(), sebl.getX(), sebl.getY(), btml.getX(), btml.getY(), btmr.getX(), btmr.getY());
        sbotr = Mathf.intersectPoint(setr.getX(), setr.getY(), sebr.getX(), sebr.getY(), btml.getX(), btml.getY(), btmr.getX(), btmr.getY());

        crossMagnitude = Mathf.pointLine(ebtml.getX(), ebtml.getY(), topl.getX(), topl.getY(), topr.getX(), topr.getY());

        baseObject = new GameObject(Vector3f.zero(), build());
    }

    private Vector2f getExtended(Vector2f a, Vector2f b, Vector2f c, Vector3f abnor, Vector3f bcnor, float extention) {
        Vector2f eab = new Vector2f(a.getX() + abnor.getX() * extention, a.getY() + abnor.getZ() * extention);
        Vector2f eba = new Vector2f(b.getX() + abnor.getX() * extention, b.getY() + abnor.getZ() * extention);

        Vector2f ebc = new Vector2f(b.getX() + bcnor.getX() * extention, b.getY() + bcnor.getZ() * extention);
        Vector2f ecb = new Vector2f(c.getX() + bcnor.getX() * extention, c.getY() + bcnor.getZ() * extention);

        return Mathf.intersectPoint(eab.getX(), eab.getY(), eba.getX(), eba.getY(), ebc.getX(), ebc.getY(), ecb.getX(), ecb.getY());
    }

    private void calculateSideNormals() {
        Vector3f nsnor = getSideNormal(stl, str);
        Vector3f lrnor = getSideNormal(str, sbr);
        tnor = nsnor.times(-1);
        bnor = nsnor;
        rnor = lrnor.times(-1);
        lnor = lrnor;
    }

    private void calculateTopNormal() {
        nnor = getTopNormal(ntl, nbl, nbr).normalize();
    }

    private Vector3f getTopNormal(Vector3f a, Vector3f b, Vector3f c) {
        Vector3f sa = Vector3f.subtract(b, a), sb = Vector3f.subtract(c, a);
        return Vector3f.cross(sa, sb).normalize();
    }

    private Vector3f getSideNormal(Vector3f a, Vector3f b) {
        Vector3f c = Vector3f.add(a, Vector3f.oneY());
        Vector3f sa = Vector3f.subtract(b, a), sb = Vector3f.subtract(c, a);
        return Vector3f.cross(sa, sb).normalize();
    }

    private Mesh build() {
        Vector3f ful, fur, fbl, fbr, bbl, bbr;

        if (!mesh) {
            return new Mesh(new Vertex[]{}, new int[]{}, Material.DEFAULT);
        }

        if (direction == 0) {
            ful = ntl;
            fur = ntr;
            fbl = stl;
            fbr = str;
            bbl = sbl;
            bbr = sbr;
        } else if (direction == 1) {
            ful = ntr;
            fur = nbr;
            fbl = str;
            fbr = sbr;
            bbl = stl;
            bbr = sbl;
        } else if (direction == 2) {
            ful = nbr;
            fur = nbl;
            fbl = sbr;
            fbr = sbl;
            bbl = str;
            bbr = stl;
        } else {
            ful = ntl;
            fur = nbl;
            fbl = stl;
            fbr = sbl;
            bbl = str;
            bbr = sbr;
        }

        float lrScale = Vector2f.subtract(tl, tr).magnitude();
        float udScale = Vector2f.subtract(tl, bl).magnitude();
        float yScale = ntl.getY() - stl.getY();

        Mesh mesh = new Mesh(new Vertex[] {
                // FRONT
                new Vertex(new Vector3f(ful.getX(), ful.getY(), ful.getZ()), frontnormal, new Vector2f(0, yScale / tiling)),
                new Vertex(new Vector3f(fbl.getX(), fbl.getY(), fbl.getZ()), frontnormal, new Vector2f(0, 0)),
                new Vertex(new Vector3f(fbr.getX(), fbr.getY(), fbr.getZ()), frontnormal, new Vector2f(lrScale / tiling, 0)),
                new Vertex(new Vector3f(fur.getX(), fur.getY(), fur.getZ()), frontnormal, new Vector2f(lrScale / tiling, yScale / tiling)),

                new Vertex(new Vector3f(ful.getX(), ful.getY(), ful.getZ()), nnor, new Vector2f(0, 0)),
                new Vertex(new Vector3f(bbl.getX(), bbl.getY(), bbl.getZ()), nnor, new Vector2f(0, udScale / tiling)),
                new Vertex(new Vector3f(bbr.getX(), bbr.getY(), bbr.getZ()), nnor, new Vector2f(lrScale / tiling, udScale / tiling)),
                new Vertex(new Vector3f(fur.getX(), fur.getY(), fur.getZ()), nnor, new Vector2f(lrScale / tiling, 0)),

                new Vertex(new Vector3f(fbl.getX(), fbl.getY(), fbl.getZ()), new Vector3f(0, -1, 0), new Vector2f(0, udScale / tiling)),
                new Vertex(new Vector3f(bbl.getX(), bbl.getY(), bbl.getZ()), new Vector3f(0, -1, 0), new Vector2f(0, 0)),
                new Vertex(new Vector3f(bbr.getX(), bbr.getY(), bbr.getZ()), new Vector3f(0, -1, 0), new Vector2f(lrScale / tiling, 0)),
                new Vertex(new Vector3f(fbr.getX(), fbr.getY(), fbr.getZ()), new Vector3f(0, -1, 0), new Vector2f(lrScale / tiling, udScale / tiling)),

                new Vertex(new Vector3f(ful.getX(), ful.getY(), ful.getZ()), leftnormal, new Vector2f(udScale / tiling, 0)),
                new Vertex(new Vector3f(fbl.getX(), fbl.getY(), fbl.getZ()), leftnormal, new Vector2f(udScale / tiling, yScale / tiling)),
                new Vertex(new Vector3f(bbl.getX(), bbl.getY(), bbl.getZ()), leftnormal, new Vector2f(0, yScale / tiling)),

                new Vertex(new Vector3f(fur.getX(), fur.getY(), fur.getZ()), rightnormal, new Vector2f(0, yScale / tiling)),
                new Vertex(new Vector3f(fbr.getX(), fbr.getY(), fbr.getZ()), rightnormal, new Vector2f(0, 0)),
                new Vertex(new Vector3f(bbr.getX(), bbr.getY(), bbr.getZ()), rightnormal, new Vector2f(udScale / tiling, 0)),

        }, new int[] {
                0,  2,  1,
                0,  3,  2,

                4,  6,  5,
                4,  7,  6,

                8,  10, 9,
                8,  11, 10,

                12, 14, 13,

                15, 17, 16

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

        getHeightAt(newPosition);

        if ((position.getY() - radius <= stl.getY() + this.height && position.getY() + height + radius >= stl.getY())) {
            newPosition = sideCollision(newPosition, topl, topr, etopr, etopl, frontnormal, false);
            newPosition = sideCollision(newPosition, btml, topl, etopl, ebtml, leftnormal, true);
            newPosition = sideCollision(newPosition, btmr, topr, etopr, ebtmr, rightnormal, true);
        }

        if (newPosition.equals(position)) {
            if (isWithinRampBounds(new Vector2f(newPosition.getX(), newPosition.getZ()))) {
                if (newPosition.getY() > stl.getY() - radius) {
                    if (newPosition.getY() < getHeightAt(position) + radius + 0.05f) {
                        Vector2f prev = new Vector2f(previous.getX(), previous.getZ()), pos = new Vector2f(newPosition.getX(), newPosition.getZ());
                        Vector2f dist = Vector2f.subtract(pos, prev);
                        Vector3f newerPosition = new Vector3f(prev.getX() + dist.getX(), newPosition.getY(), prev.getY() + dist.getY());
                        return new Collision(new Vector3f(newerPosition.getX(), getHeightAt(newerPosition) + radius, newerPosition.getZ()), velocity, true);
                    }
                }
            }
        }

        return new Collision(newPosition, velocity, isGrounded(newPosition));
    }

    private boolean isWithinBounds(Vector2f position) {
        return Mathf.pointTriangle(position, setl, sebl, sebr) || Mathf.pointTriangle(position, setl, sebr, setr);
    }

    private boolean isWithinRampBounds(Vector2f position) {
        return Mathf.pointTriangle(position, stopl, sebl, sebr) || Mathf.pointTriangle(position, stopl, sebr, stopr);
    }

    @Override
    public boolean isGrounded(Vector3f pos) {
        if (isWithinBounds(new Vector2f(pos.getX(), pos.getZ()))) {
            if (pos.getY() > stl.getY()) {
                if (pos.getY() - 0.1f < getHeightAt(pos)) {
                    return true;
                }
            }
        }
        return false;
    }

    public float getHeightAt(Vector3f pos) {
        float distance1 = Mathf.pointLine(pos.getX(), pos.getZ(), intopl.getX(), intopl.getY(), intopr.getX(), intopr.getY());
        float distance2 = Mathf.pointLine(pos.getX(), pos.getZ(), ebtml.getX(), ebtml.getY(), ebtmr.getX(), ebtmr.getY());
        float distance = ((distance2 / crossMagnitude) + (1 - distance1 / crossMagnitude)) / 2;
    return Math.min(Mathf.lerp(stl.getY(), stl.getY() + height + PlayerMovement.PLAYER_RADIUS, distance), stl.getY() + height + 0.1f);
    }

    private Vector3f sideCollision(Vector3f pos, Vector2f a, Vector2f b, Vector2f c, Vector2f d, Vector3f normal, boolean checkHeight) {
        if (Mathf.pointTrapezoid(new Vector2f(pos.getX(), pos.getZ()), a, b, c, d) && (!checkHeight || (pos.getY() < getHeightAt(pos) + PlayerMovement.PLAYER_RADIUS && pos.getY() < stl.getY() + height))) {
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
