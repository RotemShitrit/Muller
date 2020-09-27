package com.kp.meganet.meganetkp;

/**
 * Created by alex on 11/22/2015.
 */
public class MeganetInstances {
    private static MeganetInstances ourInstance = new MeganetInstances();

    public static MeganetInstances getInstance() {
        return ourInstance;
    }

    MeganetEngine _meganetEngine;
    MeganetDB _meganetDb;

    private MeganetInstances() {

    }

    public void SetMeganetEngine(MeganetEngine engine_prm)
    {
        _meganetEngine = engine_prm;
    }
    public void SetMeganetDb(MeganetDB db_prm)
    {
        _meganetDb = db_prm;
    }

    public MeganetEngine GetMeganetEngine()
    {
        return _meganetEngine;
    }
    public MeganetDB GetMeganetDb()
    {
        return _meganetDb;
    }
}
