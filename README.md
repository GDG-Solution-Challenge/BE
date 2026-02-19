# mamatolmi

mamatolmi의 백엔드 서버입니다.

<br>

## 🛠 Tech Stack


- **Language**: Java 17
- **Framework**: Spring Boot 3.x
- **Database**: MySQL
- **ORM**: Spring Data JPA
- **Infrastructure**: GCP

<br>

## 🌿Branch Strategy
본 프로젝트는 **Lightweight Git Flow**를 따릅니다.

### 📌 Branch Types
| Branch | Description |
|--------|------------|
| `main` | 운영/배포 브랜치 (항상 안정 상태 유지) |
| `develop` | 개발 통합 브랜치 |
| `feature/*` | 기능 개발 브랜치 |
| `fix/*` | 버그 수정 브랜치 |
| `refactor/*` | 리팩토링 브랜치 |

<br>

### 💡 Branch Naming Convention

- **형식**: `{type}/{description}`
- 전체 소문자
- 공백은 `-`로 연결
- 이슈 번호는 **PR에서 연결**
- **예시**:
    - `feature/user-login`
    - `refactor/db-schema`

```bash
git checkout develop
git pull origin develop
git checkout -b feature/user-login
```

<br>

### 🚦Branch Rules

- `main`
    - 직접 커밋 ❌
    - 배포 시점에만 `develop`에서 merge

- `develop`
    - 직접 커밋 ❌
    - 모든 기능 PR의 대상 브랜치

- `feature/*`
    - `develop` 기준으로 생성
    - 기능 단위 작업
    - 작업 완료 후 PR → `develop`

<br>

## 📝 Commit Convention
커밋 메시지는 아래 컨벤션을 따릅니다.

### 📌 Types
| Type     | 설명                     |
| -------- | ---------------------- |
| feat     | 새로운 기능 추가              |
| fix      | 버그 수정                  |
| refactor | 코드 리팩토링 (기능 변화 없음)     |
| docs     | 문서 수정 (README 등)       |
| style    | 코드 스타일 수정 (세미콜론, 공백 등) |
| test     | 테스트 코드 추가/수정           |
| chore    | 빌드, 설정, 패키지 관리         |
| perf     | 성능 개선                  |
| ci       | CI/CD 관련 수정            |

<br>

## 🔗 Issue Linking
이슈는 **Pull Request에서** 연결합니다.
```bash
Closes #12
Related to #8
```

## 🔄 Development Flow

본 프로젝트는 **이슈 기반 개발 흐름**을 따릅니다.

```text
Issue 생성
 → develop 기준 feature 브랜치 생성
 → 기능 개발 및 커밋
 → Pull Request 생성 (to develop)
 → Code Review
 → Merge
```
<br>