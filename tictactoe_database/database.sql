PGDMP  +    :        	        }         	   tictactoe    17.4    17.4     �           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                           false            �           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                           false            �           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                           false            �           1262    16385 	   tictactoe    DATABASE     o   CREATE DATABASE tictactoe WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'en-US';
    DROP DATABASE tictactoe;
                     postgres    false            �            1259    16551    account    TABLE     X   CREATE TABLE public.account (
    username text NOT NULL,
    password text NOT NULL
);
    DROP TABLE public.account;
       public         heap r       postgres    false            �            1259    16574    rank    TABLE     _   CREATE TABLE public.rank (
    username text NOT NULL,
    elo integer DEFAULT 100 NOT NULL
);
    DROP TABLE public.rank;
       public         heap r       postgres    false            �          0    16551    account 
   TABLE DATA           5   COPY public.account (username, password) FROM stdin;
    public               postgres    false    217   �
       �          0    16574    rank 
   TABLE DATA           -   COPY public.rank (username, elo) FROM stdin;
    public               postgres    false    218   �
       ]           2606    16557    account Account_pkey 
   CONSTRAINT     Z   ALTER TABLE ONLY public.account
    ADD CONSTRAINT "Account_pkey" PRIMARY KEY (username);
 @   ALTER TABLE ONLY public.account DROP CONSTRAINT "Account_pkey";
       public                 postgres    false    217            _           2606    16583    rank Rank_pkey 
   CONSTRAINT     T   ALTER TABLE ONLY public.rank
    ADD CONSTRAINT "Rank_pkey" PRIMARY KEY (username);
 :   ALTER TABLE ONLY public.rank DROP CONSTRAINT "Rank_pkey";
       public                 postgres    false    218            `           2606    16584    rank username    FK CONSTRAINT        ALTER TABLE ONLY public.rank
    ADD CONSTRAINT username FOREIGN KEY (username) REFERENCES public.account(username) NOT VALID;
 7   ALTER TABLE ONLY public.rank DROP CONSTRAINT username;
       public               postgres    false    217    218    4701            �   2   x�stq�442�JL��̃����	WFiq~���%P�4���Ȑ+F��� ,1{      �   /   x�KL)M�440�J,S���y���L �Ѐ��ř�Ҁ+F��� .�%     