package com.neucore.neusdk_demo.ecport;

public class JavaBean {
    private String a_create_time;
    private String b_conductivity;
    private String c_material_ph;
    private String d_tensile_strength_r;
    private String e_elongation_r;
    private String f_tensile_strength_h;
    private String g_elongation_h;

    public JavaBean(String a_create_time, String b_conductivity, String c_material_ph, String d_tensile_strength_r, String e_elongation_r, String f_tensile_strength_h, String g_elongation_h) {
        this.a_create_time = a_create_time;
        this.b_conductivity = b_conductivity;
        this.c_material_ph = c_material_ph;
        this.d_tensile_strength_r = d_tensile_strength_r;
        this.e_elongation_r = e_elongation_r;
        this.f_tensile_strength_h = f_tensile_strength_h;
        this.g_elongation_h = g_elongation_h;
    }

    public String getA_create_time() {
        return a_create_time;
    }

    public void setA_create_time(String a_create_time) {
        this.a_create_time = a_create_time;
    }

    public String getB_conductivity() {
        return b_conductivity;
    }

    public void setB_conductivity(String b_conductivity) {
        this.b_conductivity = b_conductivity;
    }

    public String getC_material_ph() {
        return c_material_ph;
    }

    public void setC_material_ph(String c_material_ph) {
        this.c_material_ph = c_material_ph;
    }

    public String getD_tensile_strength_r() {
        return d_tensile_strength_r;
    }

    public void setD_tensile_strength_r(String d_tensile_strength_r) {
        this.d_tensile_strength_r = d_tensile_strength_r;
    }

    public String getE_elongation_r() {
        return e_elongation_r;
    }

    public void setE_elongation_r(String e_elongation_r) {
        this.e_elongation_r = e_elongation_r;
    }

    public String getF_tensile_strength_h() {
        return f_tensile_strength_h;
    }

    public void setF_tensile_strength_h(String f_tensile_strength_h) {
        this.f_tensile_strength_h = f_tensile_strength_h;
    }

    public String getG_elongation_h() {
        return g_elongation_h;
    }

    public void setG_elongation_h(String g_elongation_h) {
        this.g_elongation_h = g_elongation_h;
    }
}
