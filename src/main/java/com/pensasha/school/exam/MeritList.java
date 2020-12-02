package com.pensasha.school.exam;

public class MeritList {

	private String firstname;
	private String secondname;
	private String admNo;
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
	private int and;
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
	
	public MeritList(String firstname, String secondname, String admNo, int maths, int eng, int kis, int bio, int chem,
			int phy, int hist, int cre, int geo, int ire, int hre, int hsci, int and, int agric, int comp, int avi,
			int elec, int pwr, int wood, int metal, int bc, int fren, int germ, int arab, int msc, int bs, int dnd,
			int total, int average, int points, String grade) {
		super();
		this.firstname = firstname;
		this.secondname = secondname;
		this.admNo = admNo;
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
		this.and = and;
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
	}
	
	public MeritList(int maths, int eng, int kis, int bio, int chem, int phy, int hist, int cre, int geo, int ire,
			int hre, int hsci, int and, int agric, int comp, int avi, int elec, int pwr, int wood, int metal, int bc,
			int fren, int germ, int arab, int msc, int bs, int dnd, int total) {
		super();
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
		this.and = and;
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
	}



	public MeritList() {
		super();
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getSecondname() {
		return secondname;
	}

	public void setSecondname(String secondname) {
		this.secondname = secondname;
	}

	public String getAdmNo() {
		return admNo;
	}

	public void setAdmNo(String admNo) {
		this.admNo = admNo;
	}

	public int getMaths() {
		return maths;
	}

	public void setMaths(int maths) {
		this.maths = maths;
	}

	public int getEng() {
		return eng;
	}

	public void setEng(int eng) {
		this.eng = eng;
	}

	public int getKis() {
		return kis;
	}

	public void setKis(int kis) {
		this.kis = kis;
	}

	public int getBio() {
		return bio;
	}

	public void setBio(int bio) {
		this.bio = bio;
	}

	public int getChem() {
		return chem;
	}

	public void setChem(int chem) {
		this.chem = chem;
	}

	public int getPhy() {
		return phy;
	}

	public void setPhy(int phy) {
		this.phy = phy;
	}

	public int getHist() {
		return hist;
	}

	public void setHist(int hist) {
		this.hist = hist;
	}

	public int getCre() {
		return cre;
	}

	public void setCre(int cre) {
		this.cre = cre;
	}

	public int getGeo() {
		return geo;
	}

	public void setGeo(int geo) {
		this.geo = geo;
	}

	public int getIre() {
		return ire;
	}

	public void setIre(int ire) {
		this.ire = ire;
	}

	public int getHre() {
		return hre;
	}

	public void setHre(int hre) {
		this.hre = hre;
	}

	public int getHsci() {
		return hsci;
	}

	public void setHsci(int hsci) {
		this.hsci = hsci;
	}

	public int getAnd() {
		return and;
	}

	public void setAnd(int and) {
		this.and = and;
	}

	public int getAgric() {
		return agric;
	}

	public void setAgric(int agric) {
		this.agric = agric;
	}

	public int getComp() {
		return comp;
	}

	public void setComp(int comp) {
		this.comp = comp;
	}

	public int getAvi() {
		return avi;
	}

	public void setAvi(int avi) {
		this.avi = avi;
	}

	public int getElec() {
		return elec;
	}

	public void setElec(int elec) {
		this.elec = elec;
	}

	public int getPwr() {
		return pwr;
	}

	public void setPwr(int pwr) {
		this.pwr = pwr;
	}

	public int getWood() {
		return wood;
	}

	public void setWood(int wood) {
		this.wood = wood;
	}

	public int getMetal() {
		return metal;
	}

	public void setMetal(int metal) {
		this.metal = metal;
	}

	public int getBc() {
		return bc;
	}

	public void setBc(int bc) {
		this.bc = bc;
	}

	public int getFren() {
		return fren;
	}

	public void setFren(int fren) {
		this.fren = fren;
	}

	public int getGerm() {
		return germ;
	}

	public void setGerm(int germ) {
		this.germ = germ;
	}

	public int getArab() {
		return arab;
	}

	public void setArab(int arab) {
		this.arab = arab;
	}

	public int getMsc() {
		return msc;
	}

	public void setMsc(int msc) {
		this.msc = msc;
	}

	public int getBs() {
		return bs;
	}

	public void setBs(int bs) {
		this.bs = bs;
	}

	public int getDnd() {
		return dnd;
	}

	public void setDnd(int dnd) {
		this.dnd = dnd;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}
	
}
