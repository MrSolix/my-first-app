--
-- PostgreSQL database dump
--

-- Dumped from database version 14.0
-- Dumped by pg_dump version 14.0

-- Started on 2022-02-01 21:15:37

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
-- TOC entry 3391 (class 0 OID 0)
-- Dependencies: 3
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA public IS 'standard public schema';


--
-- TOC entry 850 (class 1247 OID 41069)
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
-- TOC entry 220 (class 1259 OID 49209)
-- Name: authority; Type: TABLE; Schema: public; Owner: slavik
--

CREATE TABLE public.authority (
    id integer NOT NULL,
    name character varying(50) NOT NULL
);


ALTER TABLE public.authority OWNER TO slavik;

--
-- TOC entry 219 (class 1259 OID 49208)
-- Name: authority_id_seq; Type: SEQUENCE; Schema: public; Owner: slavik
--

CREATE SEQUENCE public.authority_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.authority_id_seq OWNER TO slavik;

--
-- TOC entry 3392 (class 0 OID 0)
-- Dependencies: 219
-- Name: authority_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: slavik
--

ALTER SEQUENCE public.authority_id_seq OWNED BY public.authority.id;


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
-- TOC entry 3393 (class 0 OID 0)
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
-- TOC entry 3394 (class 0 OID 0)
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
-- TOC entry 218 (class 1259 OID 49202)
-- Name: role; Type: TABLE; Schema: public; Owner: slavik
--

CREATE TABLE public.role (
    id integer NOT NULL,
    name character varying(50) NOT NULL
);


ALTER TABLE public.role OWNER TO slavik;

--
-- TOC entry 217 (class 1259 OID 49201)
-- Name: role_id_seq; Type: SEQUENCE; Schema: public; Owner: slavik
--

CREATE SEQUENCE public.role_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.role_id_seq OWNER TO slavik;

--
-- TOC entry 3395 (class 0 OID 0)
-- Dependencies: 217
-- Name: role_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: slavik
--

ALTER SEQUENCE public.role_id_seq OWNED BY public.role.id;


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
-- TOC entry 222 (class 1259 OID 49235)
-- Name: user_authority; Type: TABLE; Schema: public; Owner: slavik
--

CREATE TABLE public.user_authority (
    user_id integer NOT NULL,
    authority_id integer NOT NULL
);


ALTER TABLE public.user_authority OWNER TO slavik;

--
-- TOC entry 221 (class 1259 OID 49220)
-- Name: user_role; Type: TABLE; Schema: public; Owner: slavik
--

CREATE TABLE public.user_role (
    user_id integer NOT NULL,
    role_id integer NOT NULL
);


ALTER TABLE public.user_role OWNER TO slavik;

--
-- TOC entry 215 (class 1259 OID 41002)
-- Name: users; Type: TABLE; Schema: public; Owner: slavik
--

CREATE TABLE public.users (
    id bigint NOT NULL,
    user_name character varying(50) NOT NULL,
    password character varying(255),
    name character varying(50),
    age integer,
    user_type character varying(50)
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
-- TOC entry 3396 (class 0 OID 0)
-- Dependencies: 214
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: slavik
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- TOC entry 3207 (class 2604 OID 49212)
-- Name: authority id; Type: DEFAULT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public.authority ALTER COLUMN id SET DEFAULT nextval('public.authority_id_seq'::regclass);


--
-- TOC entry 3204 (class 2604 OID 24803)
-- Name: grades id; Type: DEFAULT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public.grades ALTER COLUMN id SET DEFAULT nextval('public.grades_id_seq'::regclass);


--
-- TOC entry 3203 (class 2604 OID 24683)
-- Name: group id; Type: DEFAULT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public."group" ALTER COLUMN id SET DEFAULT nextval('public.group_id_seq'::regclass);


--
-- TOC entry 3206 (class 2604 OID 49205)
-- Name: role id; Type: DEFAULT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public.role ALTER COLUMN id SET DEFAULT nextval('public.role_id_seq'::regclass);


--
-- TOC entry 3205 (class 2604 OID 41005)
-- Name: users id; Type: DEFAULT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- TOC entry 3383 (class 0 OID 49209)
-- Dependencies: 220
-- Data for Name: authority; Type: TABLE DATA; Schema: public; Owner: slavik
--

COPY public.authority (id, name) FROM stdin;
1	READ_SALARIES
\.


--
-- TOC entry 3375 (class 0 OID 24800)
-- Dependencies: 212
-- Data for Name: grades; Type: TABLE DATA; Schema: public; Owner: slavik
--

COPY public.grades (id, theme_name, grade, student_id) FROM stdin;
\.


--
-- TOC entry 3373 (class 0 OID 24680)
-- Dependencies: 210
-- Data for Name: group; Type: TABLE DATA; Schema: public; Owner: slavik
--

COPY public."group" (id, teacher_id) FROM stdin;
19	12
29	\N
28	\N
\.


--
-- TOC entry 3376 (class 0 OID 32784)
-- Dependencies: 213
-- Data for Name: group_student; Type: TABLE DATA; Schema: public; Owner: slavik
--

COPY public.group_student (group_id, student_id) FROM stdin;
\.


--
-- TOC entry 3381 (class 0 OID 49202)
-- Dependencies: 218
-- Data for Name: role; Type: TABLE DATA; Schema: public; Owner: slavik
--

COPY public.role (id, name) FROM stdin;
1	STUDENT
2	TEACHER
3	ADMIN
\.


--
-- TOC entry 3379 (class 0 OID 41046)
-- Dependencies: 216
-- Data for Name: salaries; Type: TABLE DATA; Schema: public; Owner: slavik
--

COPY public.salaries (teacher_id, salary) FROM stdin;
14	2000
12	5000
48	10000
\.


--
-- TOC entry 3385 (class 0 OID 49235)
-- Dependencies: 222
-- Data for Name: user_authority; Type: TABLE DATA; Schema: public; Owner: slavik
--

COPY public.user_authority (user_id, authority_id) FROM stdin;
13	1
\.


--
-- TOC entry 3384 (class 0 OID 49220)
-- Dependencies: 221
-- Data for Name: user_role; Type: TABLE DATA; Schema: public; Owner: slavik
--

COPY public.user_role (user_id, role_id) FROM stdin;
13	3
14	2
107	1
48	2
12	2
108	1
\.


--
-- TOC entry 3378 (class 0 OID 41002)
-- Dependencies: 215
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: slavik
--

COPY public.users (id, user_name, password, name, age, user_type) FROM stdin;
14	teacher1	$2a$12$3sntQS7oN3j/kLxHGtrUSOgXvd6USe3awF9.nLlTpgswWVSjjqHCG	Cheburator	124	teacher
107	qwe453	$2a$12$PGIDIEUTp2M/aoQ.NF7blO9th24vNu1YbamXdoZwy9iWinqHTMMH6	qwe	123	student
48	teacher12	$2a$12$fRb6vuqoZlPp2bdWXd4TZ.Fj./Dx5Or47AU9xJc/TKlxZpy8YYG1u	kek	12	teacher
12	teacher	$2a$12$8RWIkuNE37xVh3Qj6qPZpOAENv1FPaffdtXpSNGrWJxCoe.PVk32q	Ychitel	12	teacher
13	admin	$2a$12$GQX1oFO7dQIUHtDrS2/pNuYRfdfgt.00MWC7YzARUOSVT5Swusg1G	admin	999	admin
108	student	$2a$12$iDPdhEo8ewcqwqagAVjYJ.SMES4piBWmusiZ76uoR.vKCI1aceYBW	Slavik	26	student
\.


--
-- TOC entry 3397 (class 0 OID 0)
-- Dependencies: 219
-- Name: authority_id_seq; Type: SEQUENCE SET; Schema: public; Owner: slavik
--

SELECT pg_catalog.setval('public.authority_id_seq', 1, true);


--
-- TOC entry 3398 (class 0 OID 0)
-- Dependencies: 211
-- Name: grades_id_seq; Type: SEQUENCE SET; Schema: public; Owner: slavik
--

SELECT pg_catalog.setval('public.grades_id_seq', 170, true);


--
-- TOC entry 3399 (class 0 OID 0)
-- Dependencies: 209
-- Name: group_id_seq; Type: SEQUENCE SET; Schema: public; Owner: slavik
--

SELECT pg_catalog.setval('public.group_id_seq', 29, true);


--
-- TOC entry 3400 (class 0 OID 0)
-- Dependencies: 217
-- Name: role_id_seq; Type: SEQUENCE SET; Schema: public; Owner: slavik
--

SELECT pg_catalog.setval('public.role_id_seq', 3, true);


--
-- TOC entry 3401 (class 0 OID 0)
-- Dependencies: 214
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: slavik
--

SELECT pg_catalog.setval('public.users_id_seq', 109, true);


--
-- TOC entry 3219 (class 2606 OID 49214)
-- Name: authority authority_pkey; Type: CONSTRAINT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public.authority
    ADD CONSTRAINT authority_pkey PRIMARY KEY (id);


--
-- TOC entry 3211 (class 2606 OID 24805)
-- Name: grades grades_pkey; Type: CONSTRAINT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public.grades
    ADD CONSTRAINT grades_pkey PRIMARY KEY (id);


--
-- TOC entry 3209 (class 2606 OID 24685)
-- Name: group group_pkey; Type: CONSTRAINT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public."group"
    ADD CONSTRAINT group_pkey PRIMARY KEY (id);


--
-- TOC entry 3217 (class 2606 OID 49207)
-- Name: role role_pkey; Type: CONSTRAINT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public.role
    ADD CONSTRAINT role_pkey PRIMARY KEY (id);


--
-- TOC entry 3223 (class 2606 OID 49239)
-- Name: user_authority user_authority_pkey; Type: CONSTRAINT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public.user_authority
    ADD CONSTRAINT user_authority_pkey PRIMARY KEY (user_id, authority_id);


--
-- TOC entry 3221 (class 2606 OID 49224)
-- Name: user_role user_role_pkey; Type: CONSTRAINT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public.user_role
    ADD CONSTRAINT user_role_pkey PRIMARY KEY (user_id, role_id);


--
-- TOC entry 3213 (class 2606 OID 41009)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- TOC entry 3215 (class 2606 OID 41011)
-- Name: users users_user_name_key; Type: CONSTRAINT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_user_name_key UNIQUE (user_name);


--
-- TOC entry 3225 (class 2606 OID 41026)
-- Name: grades grades_student_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public.grades
    ADD CONSTRAINT grades_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.users(id);


--
-- TOC entry 3227 (class 2606 OID 41041)
-- Name: group_student group_student_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public.group_student
    ADD CONSTRAINT group_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.users(id);


--
-- TOC entry 3224 (class 2606 OID 41036)
-- Name: group group_teacher_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public."group"
    ADD CONSTRAINT group_teacher_id_fkey FOREIGN KEY (teacher_id) REFERENCES public.users(id);


--
-- TOC entry 3228 (class 2606 OID 41063)
-- Name: salaries salary_teacher_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public.salaries
    ADD CONSTRAINT salary_teacher_id_fkey FOREIGN KEY (teacher_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 3226 (class 2606 OID 32792)
-- Name: group_student student_group_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public.group_student
    ADD CONSTRAINT student_group_id_fkey FOREIGN KEY (group_id) REFERENCES public."group"(id);


--
-- TOC entry 3232 (class 2606 OID 49245)
-- Name: user_authority user_authority_authority_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public.user_authority
    ADD CONSTRAINT user_authority_authority_id_fkey FOREIGN KEY (authority_id) REFERENCES public.authority(id);


--
-- TOC entry 3231 (class 2606 OID 49240)
-- Name: user_authority user_authority_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public.user_authority
    ADD CONSTRAINT user_authority_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- TOC entry 3230 (class 2606 OID 49230)
-- Name: user_role user_role_role_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public.user_role
    ADD CONSTRAINT user_role_role_id_fkey FOREIGN KEY (role_id) REFERENCES public.role(id);


--
-- TOC entry 3229 (class 2606 OID 49225)
-- Name: user_role user_role_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: slavik
--

ALTER TABLE ONLY public.user_role
    ADD CONSTRAINT user_role_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id);


-- Completed on 2022-02-01 21:15:37

--
-- PostgreSQL database dump complete
--

