# Data Flow Diagrams

This document illustrates how data flows through the Linux kernel, from user applications to hardware and back.

## System Call Flow

The diagram below shows how user applications interact with the kernel through system calls.

```mermaid
flowchart TD
    User([User Application])
    
    subgraph "User Space"
        AppCode[Application Code]
        LibC[C Library/glibc]
    end
    
    subgraph "Kernel Space"
        SysCall[System Call Interface]
        Validation[Parameter Validation]
        KernelSvc[Kernel Service]
        Subsystems[Kernel Subsystems]
    end
    
    User --> AppCode
    AppCode --> LibC
    LibC --> SysCall
    
    SysCall --> Validation
    Validation --> KernelSvc
    KernelSvc --> Subsystems
    Subsystems --> KernelSvc
    KernelSvc --> SysCall
    SysCall --> LibC
    LibC --> AppCode
    AppCode --> User
```## I/O Data Flow

This diagram shows how data flows during I/O operations through the kernel's layers.

```mermaid
sequenceDiagram
    actor User
    participant App as Application
    participant VFS as Virtual File System
    participant FS as File System (e.g., ext4)
    participant Buffer as Buffer Cache
    participant Block as Block Layer
    participant Driver as Device Driver
    participant Device as Storage Device
    
    User->>App: Read File Request
    App->>VFS: read() System Call
    VFS->>FS: File System Read Operation
    
    alt Data in Cache
        FS->>Buffer: Check Buffer Cache
        Buffer-->>FS: Return Cached Data
    else Data not in Cache
        FS->>Buffer: Check Buffer Cache
        Buffer-->>FS: Cache Miss
        FS->>Block: Request Block Read
        Block->>Driver: Submit I/O Request
        Driver->>Device: Hardware Command
        Device-->>Driver: Data Transfer
        Driver-->>Block: I/O Completion
        Block-->>FS: Data Available
        FS->>Buffer: Update Buffer Cache
    end
    
    FS-->>VFS: Return Data
    VFS-->>App: System Call Return
    App-->>User: Display Data
```

## Process Lifecycle Flow

This diagram illustrates the lifecycle of a process within the Linux kernel.

```mermaid
sequenceDiagram
    participant Parent as Parent Process
    participant Kernel as Kernel Process Management
    participant Scheduler as Process Scheduler
    participant MM as Memory Management
    participant Child as Child Process
    
    Parent->>Kernel: fork() System Call
    Kernel->>MM: Allocate Process Resources
    MM->>Kernel: Resources Allocated
    Kernel->>Child: Create Process
    Kernel-->>Parent: Return Child PID
    
    alt exec() called
        Child->>Kernel: exec() System Call
        Kernel->>MM: Replace Memory Image
        MM->>Kernel: Memory Image Replaced
        Kernel-->>Child: Continue with New Program
    end
    
    loop Process Execution
        Scheduler->>Child: Schedule Time Slice
        Child->>Child: Execute Instructions
        Child->>Scheduler: Time Slice Expires
    end
    
    alt Process Exit
        Child->>Kernel: exit() System Call
        Kernel->>MM: Release Resources
        Kernel->>Child: Terminate Process
        Kernel->>Parent: Send SIGCHLD Signal
    end
    
    Parent->>Kernel: wait() System Call
    Kernel-->>Parent: Return Child Exit Status
```

## Networking Data Flow

This diagram shows how network data travels through the Linux kernel's networking stack.

```mermaid
flowchart TD
    subgraph "Application Layer"
        App[Application]
        Socket[Socket Interface]
    end
    
    subgraph "Transport Layer"
        TCP[TCP Protocol]
        UDP[UDP Protocol]
        ICMP[ICMP Protocol]
    end
    
    subgraph "Network Layer"
        IP[IP Protocol]
        Route[Routing Subsystem]
        Netfilter[Netfilter/iptables]
    end
    
    subgraph "Link Layer"
        NET_DEV[Network Device Layer]
        QDisc[QoS/Traffic Control]
        Driver[Network Driver]
    end
    
    subgraph "Hardware"
        NIC[Network Interface Card]
    end
    
    %% Outgoing path
    App -->|Write| Socket
    Socket --> TCP
    Socket --> UDP
    TCP --> IP
    UDP --> IP
    IP --> Netfilter
    Netfilter --> Route
    Route --> NET_DEV
    NET_DEV --> QDisc
    QDisc --> Driver
    Driver --> NIC
    
    %% Incoming path
    NIC -->|Interrupt| Driver
    Driver -->|NAPI| NET_DEV
    NET_DEV --> IP
    IP --> Netfilter
    Netfilter --> TCP
    Netfilter --> UDP
    Netfilter --> ICMP
    TCP --> Socket
    UDP --> Socket
    Socket -->|Read| App
```

## Memory Management Flow

This diagram illustrates how memory allocation requests are handled by the kernel.

```mermaid
sequenceDiagram
    actor User
    participant App as Application
    participant Syscall as System Call Interface
    participant MM as Memory Management
    participant VMA as Virtual Memory Areas
    participant PageAlloc as Page Allocator
    participant Buddy as Buddy System
    participant Slab as Slab Allocator
    participant Swap as Swap System
    participant Physical as Physical Memory
    
    User->>App: Memory Allocation Request
    App->>Syscall: mmap()/brk() System Call
    Syscall->>MM: Request Memory
    
    MM->>VMA: Create/Extend VMA
    VMA-->>MM: VMA Updated
    
    alt Memory Available
        MM->>PageAlloc: Allocate Pages
        PageAlloc->>Buddy: Request Free Pages
        Buddy->>Physical: Allocate Physical Memory
        Physical-->>Buddy: Pages Allocated
        Buddy-->>PageAlloc: Pages Available
        PageAlloc-->>MM: Page Allocation Complete
    else Memory Pressure
        MM->>Swap: Initiate Page Reclaim
        Swap->>Physical: Swap Out Pages
        Physical-->>Swap: Pages Freed
        Swap-->>MM: Memory Reclaimed
        MM->>PageAlloc: Retry Allocation
        PageAlloc->>Buddy: Request Free Pages
        Buddy->>Physical: Allocate Physical Memory
        Physical-->>Buddy: Pages Allocated
        Buddy-->>PageAlloc: Pages Available
        PageAlloc-->>MM: Page Allocation Complete
    end
    
    MM-->>Syscall: Memory Allocated
    Syscall-->>App: Allocation Success
    App-->>User: Memory Available for Use
    
    alt Kernel Memory Request
        MM->>Slab: Allocate Kernel Object
        Slab->>Buddy: Request Backing Pages
        Buddy-->>Slab: Pages Provided
        Slab-->>MM: Object Allocated
    end
```

## Interrupt Handling Flow

This diagram shows how the kernel processes hardware interrupts.

```mermaid
flowchart TD
    subgraph "Hardware"
        HW[Hardware Device]
        IRQ[Interrupt Controller]
    end
    
    subgraph "Kernel"
        IDT[Interrupt Descriptor Table]
        Handler[Interrupt Handler]
        ISR[Interrupt Service Routine]
        Softirq[Softirq Processing]
        Tasklet[Tasklet]
        WorkQ[Work Queue]
        Driver[Device Driver]
    end
    
    HW -->|Generate Interrupt| IRQ
    IRQ -->|Signal CPU| IDT
    IDT -->|Dispatch| Handler
    Handler -->|Acknowledge| IRQ
    Handler -->|Call Device-Specific| ISR
    ISR -->|Minimal Processing| Softirq
    ISR -->|Schedule| Tasklet
    ISR -->|Queue Work| WorkQ
    
    Softirq --> Driver
    Tasklet --> Driver
    WorkQ --> Driver
    
    Driver -->|Process Data| HW
```
