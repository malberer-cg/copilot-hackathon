# Linux Kernel Setup Instructions

This document provides comprehensive instructions for setting up a Linux kernel development environment, configuring, building, and installing a custom kernel.

## Prerequisites

### Development Environment

- **Operating System**: Linux distribution (Ubuntu, Fedora, Debian, etc.)
- **CPU**: 4+ cores recommended for faster builds
- **RAM**: 8+ GB recommended (16+ GB for faster builds)
- **Storage**: 30+ GB free space
- **Software**:
  - Git 2.0+
  - GCC 9.0+ or Clang 10.0+
  - GNU Make 3.8+
  - Flex
  - Bison
  - libelf-dev
  - libssl-dev
  - pkg-config
  - ncurses-dev
  - libdw-dev (optional, for perf)
  - Python 3.6+ (for scripts)

### Required Packages

#### For Debian/Ubuntu:

```bash
sudo apt update
sudo apt install git build-essential fakeroot libncurses-dev flex bison libssl-dev \
    libelf-dev bison libdw-dev libiberty-dev autoconf pkg-config \
    libudev-dev libpci-dev libiberty-dev autoconf dwarves
```

#### For Fedora/RHEL:

```bash
sudo dnf install git gcc gcc-c++ make ncurses-devel flex bison openssl-devel \
    elfutils-libelf-devel dwarves libdwarf-devel rpm-build
```

#### For Arch Linux:

```bash
sudo pacman -S git base-devel ncurses flex bison openssl libelf bc pahole
```

## Getting the Source Code

### Clone from Git Repository

```bash
# Clone the main Linux kernel repository
git clone https://git.kernel.org/pub/scm/linux/kernel/git/torvalds/linux.git
cd linux

# Alternatively, clone a stable branch
git clone https://git.kernel.org/pub/scm/linux/kernel/git/stable/linux.git
cd linux
git checkout linux-5.15.y  # Replace with desired stable branch
```

### Download a Release Tarball

If you prefer not to use Git, you can download a specific release tarball:

```bash
# Download the latest stable kernel
wget https://cdn.kernel.org/pub/linux/kernel/v5.x/linux-5.15.107.tar.xz

# Extract the tarball
tar xf linux-5.15.107.tar.xz
cd linux-5.15.107
```

## Kernel Configuration

The Linux kernel uses Kconfig to manage its configuration. There are several ways to configure the kernel:

### Using an Existing Configuration

```bash
# Use your distribution's current configuration
cp /boot/config-$(uname -r) .config

# Alternatively for Ubuntu:
# cp /usr/src/linux-headers-$(uname -r)/.config .config

# Update the configuration for your current kernel version
make oldconfig
# or for minimal questions:
make olddefconfig
```

### Interactive Configuration Tools

```bash
# Text-based configuration
make menuconfig

# GUI-based configuration (if you have GTK installed)
make xconfig

# QT-based configuration
make gconfig
```

### Default Configurations

```bash
# Create a default configuration for your architecture
make defconfig

# Create a minimal configuration
make tinyconfig

# Create a configuration for local development/testing
make localmodconfig  # Note: this only includes modules currently loaded
```

## Building the Kernel

After configuring the kernel, you can build it using the following commands:

### Full Build

```bash
# Build the kernel with all selected components
# -j specifies the number of parallel jobs (typically # of CPU cores + 1)
make -j$(nproc)
```

### Building Specific Components

```bash
# Build only the kernel image
make vmlinux -j$(nproc)

# Build kernel modules
make modules -j$(nproc)

# Build a specific module
make drivers/net/ethernet/intel/e1000/e1000.ko -j$(nproc)
```

### Clean Build

```bash
# Remove all built files
make clean

# Remove all generated files, configuration, and built files
make mrproper

# Remove only the editor backup and patch files
make distclean
```

### Architecture-Specific Builds

```bash
# For x86_64 architecture
make ARCH=x86_64 -j$(nproc)

# For ARM architecture
make ARCH=arm CROSS_COMPILE=arm-linux-gnueabi- -j$(nproc)

# For ARM64 architecture
make ARCH=arm64 CROSS_COMPILE=aarch64-linux-gnu- -j$(nproc)
```

## Installing the Kernel

After building the kernel, you can install it on your system:

### Debian/Ubuntu Method

```bash
# Install the modules
sudo make modules_install

# Install the kernel
sudo make install

# Update the bootloader
sudo update-grub
```

### Manual Installation

```bash
# Install the modules
sudo make modules_install

# Copy the kernel image
sudo cp arch/x86/boot/bzImage /boot/vmlinuz-custom

# Copy the System.map file
sudo cp System.map /boot/System.map-custom

# Copy the config file
sudo cp .config /boot/config-custom

# Update initramfs (for Debian/Ubuntu)
sudo update-initramfs -c -k custom

# Update GRUB configuration (for GRUB2)
sudo update-grub
```

## Kernel Development Workflow

### Making Changes

1. Create a development branch:
   ```bash
   git checkout -b my-feature
   ```

2. Make your code changes to the kernel source.

3. Build and test your changes:
   ```bash
   # Build the kernel
   make -j$(nproc)
   
   # Build modules
   make modules -j$(nproc)
   
   # Install modules for testing
   sudo make modules_install
   
   # Test your changes
   # (specific to your development goals)
   ```

### Testing Options

#### Using QEMU for Testing

QEMU allows you to test kernel changes without rebooting your host system:

```bash
# Install QEMU
sudo apt install qemu-system-x86

# Create a test disk (if you don't have one already)
qemu-img create -f qcow2 test-disk.img 10G

# Boot your kernel in QEMU
qemu-system-x86_64 -kernel arch/x86/boot/bzImage \
  -hda test-disk.img \
  -append "root=/dev/sda console=ttyS0" \
  -nographic
```

#### Using KVM for Testing

```bash
# Install KVM tools
sudo apt install qemu-kvm libvirt-daemon-system libvirt-clients bridge-utils

# Boot your kernel in KVM
kvm -kernel arch/x86/boot/bzImage \
  -hda test-disk.img \
  -append "root=/dev/sda console=ttyS0" \
  -nographic
```

#### Using UML (User Mode Linux) for Testing

```bash
# Build UML kernel
make ARCH=um -j$(nproc)

# Run UML kernel
./linux mem=1G rootfstype=hostfs rw init=/bin/bash
```

### Debugging the Kernel

#### Using KGDB

KGDB allows you to debug the kernel with GDB:

1. Configure the kernel with KGDB support:
   ```
   CONFIG_KGDB=y
   CONFIG_KGDB_SERIAL_CONSOLE=y
   ```

2. Add boot parameters for KGDB:
   ```
   kgdboc=ttyS0,115200 kgdbwait
   ```

3. Connect with GDB:
   ```bash
   gdb vmlinux
   (gdb) target remote /dev/ttyS0
   ```

#### Using ftrace

Ftrace is a powerful tracing utility in the kernel:

```bash
# Mount the debugfs filesystem if not already mounted
sudo mount -t debugfs none /sys/kernel/debug

# Enable function tracing
echo function > /sys/kernel/debug/tracing/current_tracer

# View the trace
cat /sys/kernel/debug/tracing/trace
```

## Cross-Compilation for Different Architectures

### Setting Up Cross-Compilation Toolchains

#### For ARM:

```bash
# Install ARM cross-compiler
sudo apt install gcc-arm-linux-gnueabi binutils-arm-linux-gnueabi

# Configure and build
make ARCH=arm CROSS_COMPILE=arm-linux-gnueabi- defconfig
make ARCH=arm CROSS_COMPILE=arm-linux-gnueabi- -j$(nproc)
```

#### For ARM64:

```bash
# Install ARM64 cross-compiler
sudo apt install gcc-aarch64-linux-gnu binutils-aarch64-linux-gnu

# Configure and build
make ARCH=arm64 CROSS_COMPILE=aarch64-linux-gnu- defconfig
make ARCH=arm64 CROSS_COMPILE=aarch64-linux-gnu- -j$(nproc)
```

#### For RISC-V:

```bash
# Install RISC-V cross-compiler
sudo apt install gcc-riscv64-linux-gnu binutils-riscv64-linux-gnu

# Configure and build
make ARCH=riscv CROSS_COMPILE=riscv64-linux-gnu- defconfig
make ARCH=riscv CROSS_COMPILE=riscv64-linux-gnu- -j$(nproc)
```

## Troubleshooting

### Common Build Issues

#### Missing Dependencies

```bash
# If you encounter missing dependencies, install them
sudo apt install libncurses-dev flex bison libssl-dev libelf-dev
```

#### Build Errors

```bash
# For detailed build errors
make V=1 -j$(nproc)

# To save build errors to a file
make V=1 -j$(nproc) 2>&1 | tee build-errors.log
```

#### Configuration Issues

```bash
# Check for configuration errors
make C=2 -j$(nproc)

# Run sparse static analyzer
make C=2 CF="-D__CHECK_ENDIAN__" -j$(nproc)
```

### Kernel Boot Issues

If your kernel fails to boot:

1. Boot with the previous working kernel
2. Check kernel logs: `dmesg | less`
3. Check system logs: `journalctl -b -1`
4. Add `debug` and `earlyprintk` to the kernel command line for more verbose output

## Additional Resources

### Documentation

- In-tree documentation: `Documentation/` directory
- [The Linux Kernel documentation](https://www.kernel.org/doc/html/latest/)
- [Linux Kernel Newbies](https://kernelnewbies.org/)

### Development Communities

- [Linux Kernel Mailing List (LKML)](http://vger.kernel.org/vger-lists.html#linux-kernel)
- [kernelnewbies IRC](irc://irc.oftc.net/kernelnewbies)
- [Linux Foundation](https://www.linuxfoundation.org/)

### Books

- "Linux Kernel Development" by Robert Love
- "Linux Device Drivers" by Jonathan Corbet, Alessandro Rubini, and Greg Kroah-Hartman
- "Understanding the Linux Kernel" by Daniel P. Bovet and Marco Cesati

### 3. Deploy with Helm

```bash
cd deployment/helm

# Add dependencies
helm repo add bitnami https://charts.bitnami.com/bitnami
helm dependency update ./lkm

# Install the chart
helm install lkm ./lkm \
  --namespace lkm-system \
  --values values-production.yaml
```

### 4. Verify Deployment

```bash
# Check pod status
kubectl get pods -n lkm-system

# Check services
kubectl get services -n lkm-system
```

### 5. Configure DNS and SSL

If you're using cert-manager:

```bash
# Apply certificate resource
kubectl apply -f deployment/kubernetes/certificate.yaml
```

Otherwise, configure your load balancer with SSL certificates for your domain.

### 6. Initialize the Production Database

```bash
# Run database migrations
kubectl exec -it deployment/lkm-api -n lkm-system -- npm run db:migrate

# Create initial admin user
kubectl exec -it deployment/lkm-api -n lkm-system -- npm run create:admin
```

## Environment Variables

### Core Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `NODE_ENV` | Environment (`development`, `test`, `production`) | `development` |
| `LOG_LEVEL` | Logging level | `info` |
| `DB_HOST` | PostgreSQL host | `localhost` |
| `DB_PORT` | PostgreSQL port | `5432` |
| `DB_NAME` | Database name | `lkm_dev` |
| `DB_USER` | Database username | `lkm_user` |
| `DB_PASSWORD` | Database password | - |
| `API_PORT` | API Gateway port | `3000` |
| `API_SECRET_KEY` | Secret for API encryption | - |
| `JWT_SECRET` | Secret for JWT tokens | - |
| `JWT_EXPIRATION` | JWT token expiration | `8h` |

### Storage Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `STORAGE_TYPE` | Storage type (`local`, `s3`) | `local` |
| `STORAGE_PATH` | Path for local storage | `./data` |
| `S3_ENDPOINT` | S3 endpoint URL | - |
| `S3_REGION` | S3 region | `us-east-1` |
| `S3_BUCKET` | S3 bucket name | `lkm-artifacts` |
| `S3_ACCESS_KEY` | S3 access key | - |
| `S3_SECRET_KEY` | S3 secret key | - |

### Build Service Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `BUILD_SERVICE_PORT` | Build service port | `3001` |
| `BUILD_WORKERS` | Number of concurrent build workers | `2` |
| `BUILD_TIMEOUT` | Build timeout in seconds | `3600` |
| `COMPILER_PATH` | Path to compiler executables | `/usr/bin` |

### Frontend Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `FRONTEND_PORT` | Frontend server port | `8080` |
| `API_BASE_URL` | Base URL for API requests | `http://localhost:3000/v1` |
| `ENABLE_ANALYTICS` | Enable usage analytics | `false` |

## Configuration Files

### API Gateway Configuration

File: `config/api-gateway.yaml`

```yaml
server:
  port: ${API_PORT}
  cors:
    enabled: ${API_ENABLE_CORS}
    origins: ["http://localhost:8080"]
    
routes:
  - path: /auth
    service: auth-service
    port: 3010
    
  - path: /configurations
    service: config-service
    port: 3020
    
  - path: /builds
    service: build-service
    port: 3001
    
  - path: /deployments
    service: deployment-service
    port: 3030
    
  - path: /monitoring
    service: monitoring-service
    port: 3040
```

### Build Service Configuration

File: `config/build-service.yaml`

```yaml
server:
  port: ${BUILD_SERVICE_PORT}
  
workers:
  count: ${BUILD_WORKERS}
  timeout: ${BUILD_TIMEOUT}
  
build:
  environments:
    - name: development
      compiler: gcc
      compilerVersion: 12.2.0
      optimizationLevel: 2
      
    - name: production
      compiler: gcc
      compilerVersion: 12.2.0
      optimizationLevel: 3
      
    - name: debug
      compiler: gcc
      compilerVersion: 12.2.0
      optimizationLevel: 0
      debugSymbols: true
```

## Troubleshooting

### Common Issues

#### Database Connection Failures

```bash
# Check database connection
npm run db:check-connection

# Verify database credentials in .env file
cat .env | grep DB_
```

#### Build Service Not Starting

```bash
# Check if required tools are installed
gcc --version
make --version

# Check build service logs
docker-compose logs build-service
```

#### JWT Authentication Issues

```bash
# Regenerate JWT secret
node scripts/generate-jwt-secret.js

# Update the JWT_SECRET in your .env file
```

#### Performance Issues

```bash
# Check system resources
docker stats

# Adjust worker counts in .env file
# BUILD_WORKERS=1
```

### Getting Help

If you encounter issues not covered here:

1. Check the detailed logs:
   ```bash
   npm run logs:all
   ```

2. Check for known issues in the GitHub repository:
   https://github.com/example/linux-kernel-manager/issues

3. Join our Discord community for real-time help:
   https://discord.gg/example-lkm

4. Submit a detailed bug report including:
   - Environment details (output of `npm run env:info`)
   - Steps to reproduce
   - Relevant logs
   - Expected vs. actual behavior
