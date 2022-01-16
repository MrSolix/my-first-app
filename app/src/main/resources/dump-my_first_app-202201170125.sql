--
-- PostgreSQL database dump
--

-- Dumped from database version 14.0
-- Dumped by pg_dump version 14.0

-- Started on 2022-01-17 01:25:58

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
-- TOC entry 3353 (class 0 OID 0)
-- Dependencies: 3
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA public IS 'standard public schema';


--
-- TOC entry 844 (class 1247 OID 41069)
-- Name: role_types; Type: TYPE; Schema: public; Owner: slavik
--

CREATE TYPE public.role_types AS ENUM (
    'STUDENT',
    'TEACHER',
    'ADMIN'
);


ALTER TYPE public.role_types OWNER TO slavik;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 212 (class 1259 OID 24800)
-- Name: grades; Type: TABLE; Schema: public; Owner: slavik
--

CREATE TABLE public.grades (
    id integer NOT NULL,
    theme_name character varying NOT NULL,
    grade integer NOT NULL,
    student_id integer
);


ALTER TABLE public.grades OWNER TO slavik;

--
-- TOC entry 211 (class 1259 OID 24799)
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
-- TOC entry 3354 (class 0 OID 0)
-- Dependencies: 211
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
-- TOC entry 3355 (class 0 OID 0)
-- Dependencies: 209
-- Name: group_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: slavik
--

ALTER SEQUENCE public.group_id_seq OWNED BY public."group".id;


--
-- TOC entry 213 (class 1259 OID 32784)
-- Name: group_student; Type: TABLE; Schema: public; Owner: slavik
--

CREATE TABLE public.group_student (
    group_id integer,
    student_id integer
);


ALTER TABLE public.group_student OWNER TO slavik;

--
-- TOC entry 216 (class 1259 OID 41046)
-- Name: salaries; Type: TABLE; Schema: public; Owner: slavik
--

CREATE TABLE public.salaries (
    teacher_id bigint NOT NULL,
    salary numeric NOT NULL
);


ALTER TABLE public.salaries OWNER TO slavik;

--
-- TOC entry 215 (class 1259 OID 41002)
-- Name: users; Type: TABLE; Schema: public; Owner: slavik
--

CREATE TABLE public.users (
    id bigint NOT NULL,
    user_name character varying(50) NOT NULL,
    password bytea,
    salt bytea,
    name character varying(50),
    age integer,
    roles character varying(20) NOT NULL
);


ALTER TABLE public.users OWNER TO slavik;

--
-- TOC entry 214 (class 1259 OID 41001)
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
-- TOC entry 3356 (class 0 OID 0)
-- Dependencies: 214
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: slavik
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- TOC entry 3186 (class 2604 OID 24803)
-- Name: grades id; Type: DEFAULT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public.grades ALTER COLUMN id SET DEFAULT nextval('public.grades_id_seq'::regclass);


--
-- TOC entry 3185 (class 2604 OID 24683)
-- Name: group id; Type: DEFAULT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public."group" ALTER COLUMN id SET DEFAULT nextval('public.group_id_seq'::regclass);


--
-- TOC entry 3187 (class 2604 OID 41005)
-- Name: users id; Type: DEFAULT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- TOC entry 3343 (class 0 OID 24800)
-- Dependencies: 212
-- Data for Name: grades; Type: TABLE DATA; Schema: public; Owner: slavik
--

COPY public.grades (id, theme_name, grade, student_id) FROM stdin;
119	Math	80	1
120	English	50	1
167	Math	95	38
155	Math	95	38
156	English	40	38
\.


--
-- TOC entry 3341 (class 0 OID 24680)
-- Dependencies: 210
-- Data for Name: group; Type: TABLE DATA; Schema: public; Owner: slavik
--

COPY public."group" (id, teacher_id) FROM stdin;
19	12
29	\N
28	64
\.


--
-- TOC entry 3344 (class 0 OID 32784)
-- Dependencies: 213
-- Data for Name: group_student; Type: TABLE DATA; Schema: public; Owner: slavik
--

COPY public.group_student (group_id, student_id) FROM stdin;
29	1
19	1
29	38
28	38
\.


--
-- TOC entry 3347 (class 0 OID 41046)
-- Dependencies: 216
-- Data for Name: salaries; Type: TABLE DATA; Schema: public; Owner: slavik
--

COPY public.salaries (teacher_id, salary) FROM stdin;
14	2000
12	5000
48	10000
64	10201
\.


--
-- TOC entry 3346 (class 0 OID 41002)
-- Dependencies: 215
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: slavik
--

COPY public.users (id, user_name, password, salt, name, age, roles) FROM stdin;
12	teacher	\\x6fabea0306d0fd446f058d1c25bf49fd7f45d3f4	\\xf7a1a450e9976a01	Ychitel	12	TEACHER
13	admin	\\x7ceeac7d0f663b73569e1d77d87c807715f5df29	\\xd1fd90799bf26d84	admin	999	ADMIN
14	teacher1	\\x2b581279a3f950dd093172ee0da37fae76de77fd	\\xcf3c19e155d22957	Cheburator	124	TEACHER
65	teacherTest	\N	\N	Cheburator	124	TEACHER
67	studentTest	\N	\N	Cheburator	124	STUDENT
68	studentTest1	\N	\N	Cheburator	124	STUDENT
48	teacher12	\\x2b581279a3f950dd093172ee0da37fae76de77fd	\\xcf3c19e155d22957	kek	12	TEACHER
11	student1	\\x9a8cf5bbef3e08654e01de68ae37c1cebc9bdecf	\\xd6997c9cc9293709	qwe	81	STUDENT
38	qwe	\\x5e74e8df6c26193eb4827ce4b98a026446bd1d5c	\\xa0f2ed6f3f87585d	qwe	123	STUDENT
64	test2	\N	\N	albert	12	TEACHER
1	student	\\xb957f84be2c8fd85a7444b6adc930e873a10691a	\\x89a2656aa04c13e4	Slavik	25	STUDENT
\.


--
-- TOC entry 3357 (class 0 OID 0)
-- Dependencies: 211
-- Name: grades_id_seq; Type: SEQUENCE SET; Schema: public; Owner: slavik
--

SELECT pg_catalog.setval('public.grades_id_seq', 167, true);


--
-- TOC entry 3358 (class 0 OID 0)
-- Dependencies: 209
-- Name: group_id_seq; Type: SEQUENCE SET; Schema: public; Owner: slavik
--

SELECT pg_catalog.setval('public.group_id_seq', 29, true);


--
-- TOC entry 3359 (class 0 OID 0)
-- Dependencies: 214
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: slavik
--

SELECT pg_catalog.setval('public.users_id_seq', 81, true);


--
-- TOC entry 3191 (class 2606 OID 24805)
-- Name: grades grades_pkey; Type: CONSTRAINT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public.grades
    ADD CONSTRAINT grades_pkey PRIMARY KEY (id);


--
-- TOC entry 3189 (class 2606 OID 24685)
-- Name: group group_pkey; Type: CONSTRAINT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public."group"
    ADD CONSTRAINT group_pkey PRIMARY KEY (id);


--
-- TOC entry 3193 (class 2606 OID 41009)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- TOC entry 3195 (class 2606 OID 41011)
-- Name: users users_user_name_key; Type: CONSTRAINT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_user_name_key UNIQUE (user_name);


--
-- TOC entry 3197 (class 2606 OID 41026)
-- Name: grades grades_student_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public.grades
    ADD CONSTRAINT grades_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.users(id);


--
-- TOC entry 3199 (class 2606 OID 41041)
-- Name: group_student group_student_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public.group_student
    ADD CONSTRAINT group_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.users(id);


--
-- TOC entry 3196 (class 2606 OID 41036)
-- Name: group group_teacher_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public."group"
    ADD CONSTRAINT group_teacher_id_fkey FOREIGN KEY (teacher_id) REFERENCES public.users(id);


--
-- TOC entry 3200 (class 2606 OID 41063)
-- Name: salaries salary_teacher_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public.salaries
    ADD CONSTRAINT salary_teacher_id_fkey FOREIGN KEY (teacher_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 3198 (class 2606 OID 32792)
-- Name: group_student student_group_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public.group_student
    ADD CONSTRAINT student_group_id_fkey FOREIGN KEY (group_id) REFERENCES public."group"(id);


-- Completed on 2022-01-17 01:25:59

--
-- PostgreSQL database dump complete
--

