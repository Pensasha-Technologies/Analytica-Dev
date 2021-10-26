package com.pensasha.school.exam;

public class MeritList {
    private String firstname;
    private String secondname;
    private String admNo;
    private String gender;
    private int kcpe;
    private String stream;
    private int maths;
    private int eng;
    private int kis;
    private int bio;
    private int chem;
    private int phy;
    private int hist;
    private int cre;
    private int geo;
    private int ire;
    private int hre;
    private int hsci;
    private int anD;
    private int agric;
    private int comp;
    private int avi;
    private int elec;
    private int pwr;
    private int wood;
    private int metal;
    private int bc;
    private int fren;
    private int germ;
    private int arab;
    private int msc;
    private int bs;
    private int dnd;
    private int total;
    private float average;
    private float deviation;
    private int rank;
    private byte points;
    private float va;
    private String grade;

    public MeritList(String firstname, String secondname, String admNo, int kcpe, String stream, int maths, int eng, int kis, int bio, int chem, int phy, int hist, int cre, int geo, int ire, int hre, int hsci, int anD, int agric, int comp, int avi, int elec, int pwr, int wood, int metal, int bc, int fren, int germ, int arab, int msc, int bs, int dnd, int total, float average, float deviation, int rank) {
        this.firstname = firstname;
        this.secondname = secondname;
        this.admNo = admNo;
        this.kcpe = kcpe;
        this.stream = stream;
        this.maths = maths;
        this.eng = eng;
        this.kis = kis;
        this.bio = bio;
        this.chem = chem;
        this.phy = phy;
        this.hist = hist;
        this.cre = cre;
        this.geo = geo;
        this.ire = ire;
        this.hre = hre;
        this.hsci = hsci;
        this.anD = anD;
        this.agric = agric;
        this.comp = comp;
        this.avi = avi;
        this.elec = elec;
        this.pwr = pwr;
        this.wood = wood;
        this.metal = metal;
        this.bc = bc;
        this.fren = fren;
        this.germ = germ;
        this.arab = arab;
        this.msc = msc;
        this.bs = bs;
        this.dnd = dnd;
        this.total = total;
        this.average = average;
        this.deviation = deviation;
        this.rank = rank;
    }

    public MeritList(int maths, int eng, int kis, int bio, int chem, int phy, int hist, int cre, int geo, int ire, int hre, int hsci, int and, int agric, int comp, int avi, int elec, int pwr, int wood, int metal, int bc, int fren, int germ, int arab, int msc, int bs, int dnd, int total, float average) {
        this.maths = maths;
        this.eng = eng;
        this.kis = kis;
        this.bio = bio;
        this.chem = chem;
        this.phy = phy;
        this.hist = hist;
        this.cre = cre;
        this.geo = geo;
        this.ire = ire;
        this.hre = hre;
        this.hsci = hsci;
        this.anD = and;
        this.agric = agric;
        this.comp = comp;
        this.avi = avi;
        this.elec = elec;
        this.pwr = pwr;
        this.wood = wood;
        this.metal = metal;
        this.bc = bc;
        this.fren = fren;
        this.germ = germ;
        this.arab = arab;
        this.msc = msc;
        this.bs = bs;
        this.dnd = dnd;
        this.total = total;
        this.average = average;
    }

    public MeritList(String firstname, String secondname, String admNo, String stream) {
        this.firstname = firstname;
        this.secondname = secondname;
        this.admNo = admNo;
        this.stream = stream;
    }

    public MeritList() {
    }

    public String getFirstname() {
        return this.firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSecondname() {
        return this.secondname;
    }

    public void setSecondname(String secondname) {
        this.secondname = secondname;
    }

    public String getAdmNo() {
        return this.admNo;
    }

    public void setAdmNo(String admNo) {
        this.admNo = admNo;
    }

    public int getMaths() {
        return this.maths;
    }

    public void setMaths(int maths) {
        this.maths = maths;
    }

    public int getEng() {
        return this.eng;
    }

    public void setEng(int eng) {
        this.eng = eng;
    }

    public int getKis() {
        return this.kis;
    }

    public void setKis(int kis) {
        this.kis = kis;
    }

    public int getBio() {
        return this.bio;
    }

    public void setBio(int bio) {
        this.bio = bio;
    }

    public int getChem() {
        return this.chem;
    }

    public void setChem(int chem) {
        this.chem = chem;
    }

    public int getPhy() {
        return this.phy;
    }

    public void setPhy(int phy) {
        this.phy = phy;
    }

    public int getHist() {
        return this.hist;
    }

    public void setHist(int hist) {
        this.hist = hist;
    }

    public int getCre() {
        return this.cre;
    }

    public void setCre(int cre) {
        this.cre = cre;
    }

    public int getGeo() {
        return this.geo;
    }

    public void setGeo(int geo) {
        this.geo = geo;
    }

    public int getIre() {
        return this.ire;
    }

    public void setIre(int ire) {
        this.ire = ire;
    }

    public int getHre() {
        return this.hre;
    }

    public void setHre(int hre) {
        this.hre = hre;
    }

    public int getHsci() {
        return this.hsci;
    }

    public void setHsci(int hsci) {
        this.hsci = hsci;
    }

    public int getAnD() {
        return this.anD;
    }

    public void setAnD(int anD) {
        this.anD = anD;
    }

    public int getAgric() {
        return this.agric;
    }

    public void setAgric(int agric) {
        this.agric = agric;
    }

    public int getComp() {
        return this.comp;
    }

    public void setComp(int comp) {
        this.comp = comp;
    }

    public int getAvi() {
        return this.avi;
    }

    public void setAvi(int avi) {
        this.avi = avi;
    }

    public int getElec() {
        return this.elec;
    }

    public void setElec(int elec) {
        this.elec = elec;
    }

    public int getPwr() {
        return this.pwr;
    }

    public void setPwr(int pwr) {
        this.pwr = pwr;
    }

    public int getWood() {
        return this.wood;
    }

    public void setWood(int wood) {
        this.wood = wood;
    }

    public int getMetal() {
        return this.metal;
    }

    public void setMetal(int metal) {
        this.metal = metal;
    }

    public int getBc() {
        return this.bc;
    }

    public void setBc(int bc) {
        this.bc = bc;
    }

    public int getFren() {
        return this.fren;
    }

    public void setFren(int fren) {
        this.fren = fren;
    }

    public int getGerm() {
        return this.germ;
    }

    public void setGerm(int germ) {
        this.germ = germ;
    }

    public int getArab() {
        return this.arab;
    }

    public void setArab(int arab) {
        this.arab = arab;
    }

    public int getMsc() {
        return this.msc;
    }

    public void setMsc(int msc) {
        this.msc = msc;
    }

    public int getBs() {
        return this.bs;
    }

    public void setBs(int bs) {
        this.bs = bs;
    }

    public int getDnd() {
        return this.dnd;
    }

    public void setDnd(int dnd) {
        this.dnd = dnd;
    }

    public int getTotal() {
        return this.total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public float getAverage() {
        return this.average;
    }

    public void setAverage(float average) {
        this.average = average;
    }

    public int getKcpe() {
        return this.kcpe;
    }

    public void setKcpe(int kcpe) {
        this.kcpe = kcpe;
    }

    public String getStream() {
        return this.stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public float getDeviation() {
        return this.deviation;
    }

    public void setDeviation(float deviation) {
        this.deviation = deviation;
    }

    public int getRank() {
        return this.rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public byte getPoints() {
        return points;
    }

    public void setPoints(byte points) {
        this.points = points;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

	public float getVa() {
		return va;
	}

	public void setVa(float va) {
		this.va = va;
	}
    
    
}