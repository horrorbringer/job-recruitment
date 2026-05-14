# Azure VM Deployment

This project is ready to run on an Azure VM with:

- Docker pulling a prebuilt Spring Boot image
- Nginx as the reverse proxy
- Cloudflare for DNS and TLS
- Azure Database for PostgreSQL as the production database

## 1. VM prerequisites

Install Docker and Docker Compose on the VM, then clone this repository.

Only expose ports `80` and `443` in the Azure Network Security Group. Keep `8081` private.

Recommended VM software:

- Docker Engine
- Docker Compose plugin
- Git

## 2. Publish the Docker image

Push the repository to GitHub and use the included GitHub Actions workflow:

- workflow file: `.github/workflows/docker-publish.yml`
- registry: `ghcr.io`
- image name: `ghcr.io/<your-github-username>/<repo-name>:latest`

After the workflow succeeds, confirm the image exists in the repository packages page on GitHub.

## 3. Prepare environment

Copy the example env file and set production values:

```bash
cp .env.prod.example .env.prod
```

Set at least:

- `APP_IMAGE`
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `MAIL_USERNAME`
- `MAIL_PASSWORD`

## 4. DNS and Cloudflare

In Cloudflare:

- create an `A` record for `@` pointing to the Azure VM public IP
- create an `A` record or `CNAME` for `www`
- enable the Cloudflare proxy if you want Cloudflare-managed HTTPS
- set SSL/TLS mode to `Full` or `Full (strict)`

In Namecheap:

- keep the domain pointed to Cloudflare nameservers

## 5. Start the stack

```bash
docker compose -f docker-compose.prod.yml pull
docker compose -f docker-compose.prod.yml up -d
```

The Nginx container listens on port `80` and proxies traffic to the Spring Boot app on the internal Docker network.

## 6. Verify

```bash
docker compose -f docker-compose.prod.yml ps
docker compose -f docker-compose.prod.yml logs -f app
docker compose -f docker-compose.prod.yml logs -f nginx
```

Then open:

- `http://your-domain.com`

If Cloudflare SSL is enabled, use:

- `https://your-domain.com`

You can also verify the origin server directly:

```bash
curl http://127.0.0.1/healthz
```

## 7. Optional auto-start on reboot

Copy the included systemd unit:

```bash
sudo mkdir -p /opt/recruitment-job
sudo cp -r . /opt/recruitment-job
sudo cp deploy/systemd/recruitment-job.service /etc/systemd/system/recruitment-job.service
sudo systemctl daemon-reload
sudo systemctl enable recruitment-job
sudo systemctl start recruitment-job
```

## 8. Updating the app

When you push new code to `main`, GitHub Actions publishes a fresh image. On the VM:

```bash
docker compose -f docker-compose.prod.yml pull
docker compose -f docker-compose.prod.yml up -d
```

## 9. Recommended production adjustments

- Replace `JPA_DDL_AUTO=update` with `validate` once the schema is stable.
- Use Azure Database for PostgreSQL instead of a Postgres container on the VM.
- Back up the `uploads` directory or move it to managed storage.
- If you disable Cloudflare proxying, terminate TLS on Nginx with Let's Encrypt.
- For Cloudflare proxy mode, prefer an origin certificate on Nginx if you want `Full (strict)`.
