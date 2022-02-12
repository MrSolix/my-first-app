--
-- PostgreSQL database dump
--

-- Dumped from database version 14.0
-- Dumped by pg_dump version 14.0

-- Started on 2021-11-28 16:26:48

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 3 (class 2615 OID 2200)
-- Name: public; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA public;


ALTER SCHEMA public OWNER TO postgres;

--
-- TOC entry 3358 (class 0 OID 0)
-- Dependencies: 3
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA public IS 'standard public schema';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 213 (class 1259 OID 24800)
-- Name: grades; Type: TABLE; Schema: public; Owner: slavik
--

CREATE TABLE public.grades (
    id integer NOT NULL,
    theme_id integer NOT NULL,
    grade integer NOT NULL,
    student_id integer
);


ALTER TABLE public.grades OWNER TO slavik;

--
-- TOC entry 212 (class 1259 OID 24799)
-- Name: grades_id_seq; Type: SEQUENCE; Schema: public; Owner: slavik
--

CREATE SEQUENCE public.grades_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.grades_id_seq OWNER TO slavik;

--
-- TOC entry 3359 (class 0 OID 0)
-- Dependencies: 212
-- Name: grades_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: slavik
--

ALTER SEQUENCE public.grades_id_seq OWNED BY public.grades.id;


--
-- TOC entry 210 (class 1259 OID 24680)
-- Name: group; Type: TABLE; Schema: public; Owner: slavik
--

CREATE TABLE public."group" (
    id integer NOT NULL,
    teacher_id integer
);


ALTER TABLE public."group" OWNER TO slavik;

--
-- TOC entry 209 (class 1259 OID 24679)
-- Name: group_id_seq; Type: SEQUENCE; Schema: public; Owner: slavik
--

CREATE SEQUENCE public.group_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.group_id_seq OWNER TO slavik;

--
-- TOC entry 3360 (class 0 OID 0)
-- Dependencies: 209
-- Name: group_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: slavik
--

ALTER SEQUENCE public.group_id_seq OWNED BY public."group".id;


--
-- TOC entry 214 (class 1259 OID 32784)
-- Name: group_student; Type: TABLE; Schema: public; Owner: slavik
--

CREATE TABLE public.group_student (
    group_id integer,
    student_id integer
);


ALTER TABLE public.group_student OWNER TO slavik;

--
-- TOC entry 217 (class 1259 OID 41046)
-- Name: salaries; Type: TABLE; Schema: public; Owner: slavik
--

CREATE TABLE public.salaries (
    teacher_id bigint NOT NULL,
    salary numeric NOT NULL
);


ALTER TABLE public.salaries OWNER TO slavik;

--
-- TOC entry 211 (class 1259 OID 24757)
-- Name: theme; Type: TABLE; Schema: public; Owner: slavik
--

CREATE TABLE public.theme (
    id integer NOT NULL,
    name character varying NOT NULL
);


ALTER TABLE public.theme OWNER TO slavik;

--
-- TOC entry 216 (class 1259 OID 41002)
-- Name: users; Type: TABLE; Schema: public; Owner: slavik
--

CREATE TABLE public.users (
    id bigint NOT NULL,
    user_name character varying(50) NOT NULL,
    password bytea,
    salt bytea,
    name character varying(50),
    age integer,
    role character varying(20) NOT NULL
);


ALTER TABLE public.users OWNER TO slavik;

--
-- TOC entry 215 (class 1259 OID 41001)
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: slavik
--

CREATE SEQUENCE public.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.users_id_seq OWNER TO slavik;

--
-- TOC entry 3361 (class 0 OID 0)
-- Dependencies: 215
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: slavik
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- TOC entry 3187 (class 2604 OID 24803)
-- Name: grades id; Type: DEFAULT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public.grades ALTER COLUMN id SET DEFAULT nextval('public.grades_id_seq'::regclass);


--
-- TOC entry 3186 (class 2604 OID 24683)
-- Name: group id; Type: DEFAULT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public."group" ALTER COLUMN id SET DEFAULT nextval('public.group_id_seq'::regclass);


--
-- TOC entry 3188 (class 2604 OID 41005)
-- Name: users id; Type: DEFAULT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- TOC entry 3348 (class 0 OID 24800)
-- Dependencies: 213
-- Data for Name: grades; Type: TABLE DATA; Schema: public; Owner: slavik
--

COPY public.grades (id, theme_id, grade, student_id) FROM stdin;
35	1	90	1
36	2	60	1
37	1	70	2
38	2	80	2
\.


--
-- TOC entry 3345 (class 0 OID 24680)
-- Dependencies: 210
-- Data for Name: group; Type: TABLE DATA; Schema: public; Owner: slavik
--

COPY public."group" (id, teacher_id) FROM stdin;
1	3
\.


--
-- TOC entry 3349 (class 0 OID 32784)
-- Dependencies: 214
-- Data for Name: group_student; Type: TABLE DATA; Schema: public; Owner: slavik
--

COPY public.group_student (group_id, student_id) FROM stdin;
1	1
1	2
\.


--
-- TOC entry 3352 (class 0 OID 41046)
-- Dependencies: 217
-- Data for Name: salaries; Type: TABLE DATA; Schema: public; Owner: slavik
--

COPY public.salaries (teacher_id, salary) FROM stdin;
3	5000
\.


--
-- TOC entry 3346 (class 0 OID 24757)
-- Dependencies: 211
-- Data for Name: theme; Type: TABLE DATA; Schema: public; Owner: slavik
--

COPY public.theme (id, name) FROM stdin;
1	Math
2	English
\.


--
-- TOC entry 3351 (class 0 OID 41002)
-- Dependencies: 216
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: slavik
--

COPY public.users (id, user_name, password, salt, name, age, role) FROM stdin;
1	student	\\x2053bc538d4115b1ad39b74cb3230135092722c2	\\x5f053ea99e1ba257	Slavik	25	student
2	student1	\\xb24d5884d6ee9846f97b3edba81e7c61b99ea95b	\\xee1c90ef6a0b9430	Gena	30	student
3	teacher	\\x52430852e2672fc8bf581b5d0d3e51b0109b1edc	\\x54a3488811385f9f	Ychitel	30	teacher
9	admin	\\xe7cdb45a9c7d40ae0a196349471ba5c9b9c81982	\\xdfb3d633a13935a0	admin	99	admin
\.


--
-- TOC entry 3362 (class 0 OID 0)
-- Dependencies: 212
-- Name: grades_id_seq; Type: SEQUENCE SET; Schema: public; Owner: slavik
--

SELECT pg_catalog.setval('public.grades_id_seq', 40, true);


--
-- TOC entry 3363 (class 0 OID 0)
-- Dependencies: 209
-- Name: group_id_seq; Type: SEQUENCE SET; Schema: public; Owner: slavik
--

SELECT pg_catalog.setval('public.group_id_seq', 16, true);


--
-- TOC entry 3364 (class 0 OID 0)
-- Dependencies: 215
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: slavik
--

SELECT pg_catalog.setval('public.users_id_seq', 9, true);


--
-- TOC entry 3194 (class 2606 OID 24805)
-- Name: grades grades_pkey; Type: CONSTRAINT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public.grades
    ADD CONSTRAINT grades_pkey PRIMARY KEY (id);


--
-- TOC entry 3190 (class 2606 OID 24685)
-- Name: group group_pkey; Type: CONSTRAINT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public."group"
    ADD CONSTRAINT group_pkey PRIMARY KEY (id);


--
-- TOC entry 3192 (class 2606 OID 24761)
-- Name: theme theme_pk; Type: CONSTRAINT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public.theme
    ADD CONSTRAINT theme_pk PRIMARY KEY (id);


--
-- TOC entry 3196 (class 2606 OID 41009)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- TOC entry 3198 (class 2606 OID 41011)
-- Name: users users_user_name_key; Type: CONSTRAINT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_user_name_key UNIQUE (user_name);


--
-- TOC entry 3201 (class 2606 OID 41026)
-- Name: grades grades_student_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public.grades
    ADD CONSTRAINT grades_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.users(id);


--
-- TOC entry 3200 (class 2606 OID 40996)
-- Name: grades grades_theme_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public.grades
    ADD CONSTRAINT grades_theme_id_fkey FOREIGN KEY (theme_id) REFERENCES public.theme(id);


--
-- TOC entry 3203 (class 2606 OID 41041)
-- Name: group_student group_student_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public.group_student
    ADD CONSTRAINT group_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.users(id);


--
-- TOC entry 3199 (class 2606 OID 41036)
-- Name: group group_teacher_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public."group"
    ADD CONSTRAINT group_teacher_id_fkey FOREIGN KEY (teacher_id) REFERENCES public.users(id);


--
-- TOC entry 3204 (class 2606 OID 41063)
-- Name: salaries salary_teacher_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public.salaries
    ADD CONSTRAINT salary_teacher_id_fkey FOREIGN KEY (teacher_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 3202 (class 2606 OID 32792)
-- Name: group_student student_group_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public.group_student
    ADD CONSTRAINT student_group_id_fkey FOREIGN KEY (group_id) REFERENCES public."group"(id);


-- Completed on 2021-11-28 16:26:48

--
-- PostgreSQL database dump complete
--

