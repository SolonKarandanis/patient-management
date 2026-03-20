-- Force change on roles
UPDATE public.roles SET name = name || ' ';
UPDATE public.roles SET name = TRIM(name);

-- Force change on user_roles (simple update usually works here as it is a join table)
UPDATE public.user_roles SET role_id = role_id;

-- Force change on users
UPDATE public.users SET first_name = first_name || ' ';
UPDATE public.users SET first_name = TRIM(first_name);
